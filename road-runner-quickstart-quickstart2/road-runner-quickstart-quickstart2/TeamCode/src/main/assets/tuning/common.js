// TODO: time-interpolate data

// https://en.wikipedia.org/wiki/Kahan_summation_algorithm#The_algorithm
function kahanSum(xs) {
  let sum = 0;
  let c = 0;

  for (let i = 0; i < xs.length; i++) {
    const y = xs[i] - c;
    const t = sum + y;
    c = (t - sum) - y;
    sum = t;
  }

  return sum;
}

// https://en.wikipedia.org/wiki/Simple_linear_regression#Simple_linear_regression_without_the_intercept_term_(single_regressor)
function fitLinearNoIntercept(xs, ys) {
  return kahanSum(
    xs.map((x, i) => x * ys[i])
  ) / kahanSum(
    xs.map(x => x * x)
  );
}

function fitLinearWithScaling(xs, ys) {
  const xOffset = xs.reduce((a, b) => a + b, 0) / xs.length;
  const yOffset = ys.reduce((a, b) => a + b, 0) / ys.length;

  const xScale = xs.reduce((acc, x) => Math.max(acc, Math.abs(x - xOffset)), 0);
  const yScale = ys.reduce((acc, y) => Math.max(acc, Math.abs(y - yOffset)), 0);

  const data = xs.map((x, i) => [(x - xOffset) / xScale, (ys[i] - yOffset) / yScale]);

  const result = regression.linear(data);
  const [m, b] = result.equation;

  return [m * yScale / xScale, b * yScale - m * xOffset * yScale / xScale + yOffset];
}

// no output for first pair
function numDerivOnline(xs, ys) {
  if (xs.length !== ys.length) {
    throw new Error(`${xs.length} !== ${ys.length}`);
  }

  return ys
    .slice(1)
    .map((y, i) => (y - ys[i]) / (xs[i + 1] - xs[i]));
}

// no output for first or last pair
function numDerivOffline(xs, ys) {
  return ys
    .slice(2)
    .map((y, i) => (y - ys[i]) / (xs[i + 2] - xs[i]));
}

const CPS_STEP = 0x10000;

function inverseOverflow(input, estimate) {
  // convert to uint16
  let real = input & 0xffff;
  // initial, modulo-based correction: it can recover the remainder of 5 of the upper 16 bits
  // because the velocity is always a multiple of 20 cps due to Expansion Hub's 50ms measurement window
  real += ((real % 20) / 4) * CPS_STEP;
  // estimate-based correction: it finds the nearest multiple of 5 to correct the upper bits by
  real += Math.round((estimate - real) / (5 * CPS_STEP)) * 5 * CPS_STEP;
  return real;
}

// no output for first or last pair
function fixVels(ts, xs, vs) {
  if (ts.length !== xs.length || ts.length !== vs.length) {
    throw new Error();
  }

  return numDerivOffline(ts, xs).map((est, i) => inverseOverflow(vs[i + 1], est));
}

// see https://github.com/FIRST-Tech-Challenge/FtcRobotController/issues/617
function fixAngVels(vs) {
  if (vs.length === 0) {
    return [];
  }

  let offset = 0;
  lastV = vs[0];
  const vsFixed = [lastV];
  for (let i = 1; i < vs.length; i++) {
    if (Math.abs(vs[i] - lastV) > Math.PI) {
      offset -= Math.sign(vs[i] - lastV) * 2 * Math.PI;
    }
    vsFixed.push(offset + vs[i]);
    lastV = vs[i];
  }

  return vsFixed;
}

// data comes in pairs
function newLinearRegressionChart(container, xs, ys, options, onChange) {
  if (xs.length !== ys.length) {
    throw new Error(`${xs.length} !== ${ys.length}`);
  }

  // cribbed from https://plotly.com/javascript/plotlyjs-events/#select-event
  const color = '#777';
  const colorLight = '#bbb';

  let mask = xs.map(() => true);

  function fit(xs, ys) {
    return options.noIntercept ? [fitLinearNoIntercept(xs, ys), 0] : fitLinearWithScaling(xs, ys);
  }

  const [m, b] = fit(xs, ys);

  if (onChange) onChange(m, b);

  const minX = xs.reduce((a, b) => Math.min(a, b), 0);
  const maxX = xs.reduce((a, b) => Math.max(a, b), 0);

  const chartDiv = document.createElement('div');
  const width = Math.max(0, window.innerWidth - 50);
  Plotly.newPlot(chartDiv, [{
    type: 'scatter',
    mode: 'markers',
    x: xs,
    y: ys,
    name: 'Samples',
    // markers seem to respond to selection 
    marker: {color: mask.map(b => b ? color : colorLight), size: 5},
  }, {
    type: 'scatter',
    mode: 'lines',
    x: [minX, maxX],
    y: [m * minX + b, m * maxX + b],
    name: 'Regression Line',
    line: {color: 'red'}
  }], {
    title: options.title || '',
    // sets the starting tool from the modebar
    dragmode: 'select',
    showlegend: false,
    hovermode: false,
    width,
    height: width * 9 / 16,
  }, {
    // 'select2d', 'zoom2d', 'pan2d', 'lasso2d', 'zoomIn2d', 'zoomOut2d', 'autoScale2d', 'resetScale2d' left
    modeBarButtonsToRemove: [],
  });

  const results = document.createElement('p');

  function setResultText(m, b) {
    results.innerText = `${options.slope || 'slope'}: ${m}, ${options.intercept || 'y-intercept'}: ${b}`;
  }
  setResultText(m, b);

  function updatePlot() {
    Plotly.restyle(chartDiv, 'marker.color', [
      mask.map(b => b ? color : colorLight)
    ], [0]);

    const [m, b] = fit(
      xs.filter((_, i) => mask[i]),
      ys.filter((_, i) => mask[i]),
    );
    setResultText(m, b);
    if (onChange) onChange(m, b);

    const minX = xs.reduce((a, b) => Math.min(a, b));
    const maxX = xs.reduce((a, b) => Math.max(a, b));

    Plotly.restyle(chartDiv, {
      x: [[minX, maxX]],
      y: [[m * minX + b, m * maxX + b]],
    }, [1]);
  }

  let pendingSelection = null;

  chartDiv.on('plotly_selected', function(eventData) {
    if (eventData === undefined) {
      return;
    }

    pendingSelection = eventData;
  });

  function applyPendingSelection(b) {
    if (pendingSelection === null) return false;

    for (const pt of pendingSelection.points) {
      mask[pt.pointIndex] = b;
    }

    Plotly.restyle(chartDiv, 'selectedpoints', [null], [0]);

    pendingSelection = null;

    return true;
  }

  const includeButton = document.createElement('button');
  includeButton.innerText = '[i]nclude';
  includeButton.addEventListener('click', () => {
    if (!applyPendingSelection(true)) return;
    updatePlot();
  });

  const excludeButton = document.createElement('button');
  excludeButton.innerText = '[e]xclude';
  excludeButton.addEventListener('click', () => {
    if (!applyPendingSelection(false)) return;
    updatePlot();
  });

  document.addEventListener('keydown', e => {
    if (e.key === 'i') {
      if (!applyPendingSelection(true)) return;
      updatePlot();
    } else if (e.key === 'e') {
      if (!applyPendingSelection(false)) return;
      updatePlot();
    }
  });

  while (container.firstChild) {
    container.removeChild(container.firstChild);
  }

  const buttons = document.createElement('div');
  buttons.appendChild(includeButton);
  buttons.appendChild(excludeButton);

  const bar = document.createElement('div');
  bar.setAttribute('class', 'bar');
  bar.appendChild(buttons);

  bar.appendChild(results);

  container.appendChild(bar);
  container.appendChild(chartDiv);

  return function(xsNew, ysNew) {
    if (xsNew.length !== ysNew.length) {
      throw new Error(`${xsNew.length} !== ${ysNew.length}`);
    }

    xs = xsNew;
    ys = ysNew;
    mask = xs.map(() => true);

    Plotly.restyle(chartDiv, {
      x: [xs],
      y: [ys],
    }, [0]);

    updatePlot();
  };
}

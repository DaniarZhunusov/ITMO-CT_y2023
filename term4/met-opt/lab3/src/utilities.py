from classes import *


def poly1d_to_string(weights: np.ndarray):
  s = []
  for i in range(weights.size):
    if i == 0:
      s.append(f"{weights[0]:.3f}")
    elif i == 1:
      s.append(f"{weights[i]:+.3f}x")
    else:
      s.append(f"{weights[i]:+.3f}x^{i}")
  return "".join(s)

def init_wine():
  wine = fetch_ucirepo(id=186)
  features = wine.data.features
  normalized = (features-features.mean())/features.std()
  targets = wine.data.targets
  return normalized, targets

def finish_wine(weights: np.array, sgd: StochasticGradientDescent, normalized: pd.DataFrame, targets: pd.DataFrame):
  print(weights)
  X = normalized.sample(150)
  estimated = sgd.compute_polynomials(weights, X).tolist()
  real = targets.loc[X.index].to_numpy().T[0].tolist()
  for value in set(real):
    x = [estimated[i] for i in range(len(estimated)) if real[i] == value]
    print(f"Type {value} lies in [{min(x):.3f}, {max(x):.3f}]")

def lib_train(input: pd.DataFrame, output: pd.DataFrame, mode: str, iter_count: int):
  opts = {
      "SGD": optimizers.SGD(learning_rate=1e-1),
      "Momentum": optimizers.SGD(learning_rate=1e-1, momentum=0.95),
      "Nesterov": optimizers.SGD(learning_rate=1e-1, momentum=0.95, nesterov=True),
      "AdaGrad": optimizers.Adagrad(learning_rate=1e-1),
      "RMSProp": optimizers.RMSprop(learning_rate=1e-1),
      "Adam": optimizers.Adam(learning_rate=1e-1)
  }
  model = models.Sequential([layers.Dense(1, input_shape=(input.shape[1],))])
  model.compile(optimizer=opts[mode], loss='mse')
  model.fit(input, output, epochs=iter_count, verbose=0)
  m_weights, m_biases = model.layers[0].get_weights()
  return np.array([m_biases[0], m_weights[0][0]])

def torch_train(input: pd.DataFrame, output: pd.DataFrame, mode: str, iter_count: int):
  input_tensor = torch.tensor(input.values, dtype=torch.float32)
  output_tensor = torch.tensor(output.values, dtype=torch.float32)
  model = torch.nn.Linear(input.shape[1], 1)
  loss_func = torch.nn.MSELoss()
  sgd = {
      "SGD": optim.SGD(model.parameters(), lr=1e-1),
      "Momentum": optim.SGD(model.parameters(), lr=1e-1, momentum=0.95),
      "Nesterov": optim.SGD(model.parameters(), lr=1e-1, momentum=0.95, nesterov=True),
      "AdaGrad": optim.Adagrad(model.parameters(), lr=1e-1),
      "RMSProp": optim.RMSprop(model.parameters(), lr=1e-1),
      "Adam": optim.Adam(model.parameters(), lr=1e-1),
  }[mode]
  with profile(
    activities=[torch.profiler.ProfilerActivity.CPU],
    with_flops=True,
    record_shapes=True
  ) as prof:
    for iter in range(iter_count):
      with record_function("full_pass"):
        new_output = model(input_tensor)
        new_loss = loss_func(new_output, output_tensor)
        sgd.zero_grad()
        new_loss.backward()
        sgd.step()
    weight = model.weight.detach().numpy()
    bias = model.bias.detach().numpy()

  events = prof.key_averages()
  flops = sum(event.flops for event in events if event.flops > 0)
  return np.concatenate((bias, weight[0])), flops

def init_plane_data(f: Callable[[float], float] = lambda x: 16 * x**3 - 24 * x**2 + 9 * x + 1):
  dots = [(x, f(x) + random.uniform(-1, 1) / 10) for x in [random.random() for _ in range(100)]]
  input = pd.DataFrame({"x": [dots[i][0] for i in range(len(dots))]})
  output = pd.DataFrame({"y": [dots[i][1] for i in range(len(dots))]})
  return dots, input, output

def init_space_data(f: Callable[[float, float], float] = lambda x, y: x + y + 1):
  dots = [(x, y, f(x, y) + random.uniform(-1, 1) / 10) for x in [random.random() for _ in range(100)] for y in [random.random() for _ in range(100)]]
  input = pd.DataFrame({"x": [dots[i][0] for i in range(len(dots))], "y": [dots[i][1] for i in range(len(dots))]})
  output = pd.DataFrame({"z": [dots[i][2] for i in range(len(dots))]})
  return dots, input, output
import matplotlib.pyplot as plt
from classes import *
from utilities import *

f = lambda x: 3 * x**2 - 3 * x + 1 #16 * x**3 - 24 * x**2 + 9 * x + 1
dots, input, output = init_plane_data(f)
x = []
y1 = []
y2 = []
y3 = []
y4 = []
for iter_count in range(10, 500, 10):
  print(f"\r{iter_count}")
  x.append(iter_count)
  sgd1 = StochasticGradientDescent(1, 2, 100, Regularizers.mean(LossFunctions.L2), Schedulers.fixed(1e-2), iter_count)
  weights = sgd1.run(input, output, False)
  y1.append(sgd1.compute_regularizer(weights, input, output))
  sgd2 = StochasticGradientDescent(1, 2, 100, Regularizers.elastic(0.5, 0.5), Schedulers.fixed(1e-2), iter_count)
  weights = sgd2.run(input, output, False)
  y2.append(sgd1.compute_regularizer(weights, input, output))
  sgd3 = StochasticGradientDescent(1, 2, 100, Regularizers.elastic(0.2, 0.8), Schedulers.fixed(1e-2), iter_count)
  weights = sgd3.run(input, output, False)
  y3.append(sgd1.compute_regularizer(weights, input, output))
  sgd4 = StochasticGradientDescent(1, 2, 100, Regularizers.elastic(0.8, 0.2), Schedulers.fixed(1e-2), iter_count)
  weights = sgd4.run(input, output, False)
  y4.append(sgd1.compute_regularizer(weights, input, output))


plt.plot(x, y1, label="Mean")
plt.plot(x, y2, label="Elastic 1:1")
plt.plot(x, y3, label="Elastic 1:4")
plt.plot(x, y4, label="Elastic 4:1")
plt.ylabel("MSE")
plt.xlabel("Iterations Count")
plt.legend()
plt.show()
import matplotlib.pyplot as plt
from classes import *
from utilities import *

f = lambda x: - 3 * x + 1 # 3 * x**2 - 3 * x + 1 #16 * x**3 - 24 * x**2 + 9 * x + 1
dots, input, output = init_plane_data(f)
x = []
y1 = []
y2 = []
y3 = []
y4 = []
y5 = []
y6 = []
sgd = StochasticGradientDescent(1, 1, 1, Regularizers.mean(LossFunctions.L2))
for iter_count in range(50, 500, 50):
  print(f"\r{iter_count}")
  x.append(iter_count)
  weights = lib_train(input, output, "SGD", iter_count)
  y1.append(sgd.compute_regularizer(weights, input, output))

  weights = lib_train(input, output, "Momentum", iter_count)
  y2.append(sgd.compute_regularizer(weights, input, output))

  weights = lib_train(input, output, "Nesterov", iter_count)
  y3.append(sgd.compute_regularizer(weights, input, output))

  weights = lib_train(input, output, "AdaGrad", iter_count)
  y4.append(sgd.compute_regularizer(weights, input, output))

  weights = lib_train(input, output, "RMSProp", iter_count)
  y5.append(sgd.compute_regularizer(weights, input, output))

  weights = lib_train(input, output, "Adam", iter_count)
  y6.append(sgd.compute_regularizer(weights, input, output))


plt.plot(x, y1, label="SGD")
plt.plot(x, y2, label="Momentum")
plt.plot(x, y3, label="Nesterov")
plt.plot(x, y4, label="AdaGrad")
plt.plot(x, y5, label="RMSProp")
plt.plot(x, y6, label="Adam")
plt.ylabel("MSE")
plt.xlabel("Iterations Count")
plt.legend()
plt.show()
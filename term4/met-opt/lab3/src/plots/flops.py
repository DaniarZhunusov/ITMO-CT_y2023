import matplotlib.pyplot as plt

f = lambda x: - 3 * x + 1 # 3 * x**2 - 3 * x + 1 #16 * x**3 - 24 * x**2 + 9 * x + 1
dots, input, output = init_plane_data(f)
x = []
y1 = []
sgd = StochasticGradientDescent(1, 1, 1, Regularizers.mean(LossFunctions.L2))
for iter_count in range(50, 1000, 50):
  print(f"\r{iter_count}")
  x.append(iter_count)
  weights, flops = torch_train(input, output, "SGD", iter_count)
  print(flops)
  y1.append(flops)


plt.plot(x, y1, label="SGD")

plt.ylabel("FLOPs")
plt.xlabel("Iterations Count")
plt.legend()
plt.show()
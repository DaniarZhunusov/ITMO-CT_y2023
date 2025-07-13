import matplotlib.pyplot as plt
import time
import tracemalloc
from classes import *
from utilities import *

tracemalloc.start()

f = lambda x: 3 * x**2 - 3 * x + 1
dots, input, output = init_plane_data()
x = [i for i in range(1, 101)]
mse = []
times = []
mem_peak = []
for i in x:
  print(f"\r{i}/100 Running", end="")
  sgd = StochasticGradientDescent(1, 2, i, Regularizers.mean(LossFunctions.L2), Schedulers.fixed(1e-1), 100)
  #tracemalloc.clear_traces()
  #start_current, start_peak = tracemalloc.get_traced_memory()
  start = time.perf_counter()
  weights = sgd.run(input, output, False)
  end = time.perf_counter()
  #end_current, end_peak = tracemalloc.get_traced_memory()
  times.append(end - start)
  #mem_peak.append(end_peak - start_current)
  mse.append(sgd.compute_regularizer(weights, input, output))
print("\r", end="")

plt.plot(x, mse)
#plt.plot(x, times)
#plt.plot(x, mem_peak)

plt.ylabel("MSE")
#plt.ylabel("Runtime")
#plt.ylabel("Memory usage")

plt.xlabel("Batch size")
plt.show()
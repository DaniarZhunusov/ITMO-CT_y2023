import matplotlib.pyplot as plt
from classes import *
from utilities import *

#f = lambda x: -3 * x + 1
#f = lambda x: 3 * x**2 - 3 * x + 1 
f = lambda x: 16 * x**3 - 24 * x**2 + 9 * x + 1
dots, input, output = init_plane_data(f)
sgd = StochasticGradientDescent(1, 3, 3, Regularizers.mean(LossFunctions.L2), Schedulers.fixed(0.5), 100000) 
# Количество требуемых эпох сильно зависит от степени полинома
weights = sgd.run(input, output)
print(poly1d_to_string(weights))
f_eval = lambda x: weights[0] + weights[1] * x + weights[2] * x**2 + weights[3] * x**3
continous = np.arange(0, 1, 1e-5)
plt.scatter([dots[i][0] for i in range(len(dots))], [dots[i][1] for i in range(len(dots))])
plt.plot(continous, f(continous), label="Real")
plt.plot(continous, f_eval(continous), label="Calculated")
plt.ylabel("Y")
plt.xlabel("X")
plt.show()
import matplotlib.pyplot as plt
from classes import *
from utilities import *

normalized, targets = init_wine()
print(normalized.size, targets.size)
weights = np.array([*torch_train(normalized, targets, "Adam")])
sgd = StochasticGradientDescent(11, 1, 1)
print(sgd.compute_regularizer(weights, normalized, targets))
finish_wine(weights, sgd, normalized, targets)
estimated = sgd.compute_polynomials(weights, normalized).tolist()
real = targets.loc[normalized.index].to_numpy().T[0].tolist()
m = {}
print(len(real))
for i in range(len(real)):
  x = round(estimated[i]) - real[i]
  if x in m:
    m[x] += 1
  else:
    m[x] = 1

labels = [f"{x:+}" for x in m.keys()]
print(list(m.values()))
print(list(m.keys()))
plt.pie(list(m.values()), labels=labels, wedgeprops={'edgecolor': 'black', 'linewidth': 2})
plt.title('Разница между правильным ответом и предсказанным')
plt.show()
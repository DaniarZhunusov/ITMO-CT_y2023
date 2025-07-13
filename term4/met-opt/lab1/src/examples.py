from drawer import *
from Grad_Dicht import *
from Grad_GSS import *


# ***Himmelblau's function***

def himmelblau(x):
    return  (x[0]**2 + x[1] - 11)**2 + (x[0] + x[1]**2 - 7)**2


def f(x):
    # return x[0]**2
    # return x[0]**2 + x[1]**2
    # return (x[0]-3)**2 +(x[1]+ 2)**2
     return x[0] ** 2 + x[1] ** 2 + (x[0] - 1) ** 2 + (x[1] - 1) ** 2
    # return np.sin(0.5 * x[0] ** 2 - 0.25 * x[1] ** 2 + 3) * np.cos(2 * x[0] + 1 - np.exp(x[1]))
    # return  (x[0] - 3)**2 + (x[1] + 2)**2 + x[0] * x[1]
    # return -x[0]**2 - x[1]**2
    # return abs(x[0]) + abs(x[1])


    # 'L-BFGS-B' LSR
    # 'BFGS' DICHT

func = himmelblau
start_pos = np.array([4, 1])
grid = np.array([-6, 6])

print_output(start_pos, GradientDescending().find_min, func, "GradientDescending", grid)
print_output(start_pos, DichtGradientDescending().find_min, func, "DichtGradientDescending", grid)
print_output(start_pos, GradientGoldenSearchSection().find_min, func, "GradientGoldenSearchSection", grid)

print_output_opt(start_pos, func, 'BFGS', grid)
print_output_opt(start_pos, func, 'CG', grid)

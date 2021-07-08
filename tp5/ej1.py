import matplotlib.pyplot as plt
from statistics import stdev
import sys

def avg(l):
    if len(l) == 0:
        return 0
    return sum(l)/len(l)


fig = plt.figure(figsize=(15,10))
ax1 = fig.add_subplot(111)
ax1.set_xlabel('Cantidad de particulas', fontsize=27)
ax1.set_ylabel(f'Tiempo (s)', fontsize=27)  # dt=1e-2, paso=1000, dt*paso = 1e-2*1000 = 10s = tiempo
ax1.tick_params(axis='both', which='major', labelsize=20, width=2.5, length=10)

ys_avg = []
ys_std = []
with open(f'ej1_r-1,00.csv') as f:
  for line in f:
    values_for_n = [float(i) for i in line.split(',')]
    ys_avg.append(avg(values_for_n))
    ys_std.append(stdev(values_for_n))

x = [n for n in range(50, 201, 15)]
print(len(x), len(ys_avg))
ax1.errorbar(x, ys_avg, fmt='o', yerr=ys_std)

# plt.legend(loc='best', fontsize=18)
# plt.yscale("log")
plt.grid()
plt.savefig('ej1')
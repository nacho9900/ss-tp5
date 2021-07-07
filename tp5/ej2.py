import matplotlib.pyplot as plt
from statistics import stdev


def avg(l):
    if len(l) == 0:
        return 0
    return sum(l)/len(l)


fig = plt.figure(figsize=(15,10))
ax1 = fig.add_subplot(111)
ax1.set_xlabel('Radio (m)', fontsize=27)
ax1.set_ylabel(f'Cantidad ya infectados a los 10s', fontsize=27)  # dt=1e-2, paso=1000, dt*paso = 1e-2*1000 = 10s = tiempo
ax1.tick_params(axis='both', which='major', labelsize=20, width=2.5, length=10)

ys_avg = []
ys_std = []
r_list = [i/10 for i in range(5, 21, 2)]  # [0.5, 0.7, 0.9, ..., 2.0]
for r in r_list:
  r = '{0:.2f}'.format(r).replace('.', ',')
  y_for_r = []
  for seed in range(10):
    with open(f'nonHealthy_r-{r}_seed-{seed}.csv') as f:
      y = [float(i) for i in f.readlines()]
      y_for_r.append(y)
  y_avg = [avg(y_time) for y_time in zip(*y_for_r)]

  # Commented version skips some deviation lines so you can see the average line
  # y_std = [stdev(y_time)*(i%100 == 0) for i, y_time in enumerate(zip(*y_for_r))]
  y_std = [stdev(y_time) for y_time in zip(*y_for_r)]
  
  ys_avg.append(y_avg)
  ys_std.append(y_std)

ys_avg_at_1000 = []
ys_std_at_1000 = []
for y_avg, y_std, r in zip(ys_avg, ys_std, r_list):
  # x = [t for t in range(len(y_avg))]
  # ax1.errorbar(x, y_avg, yerr=y_std, label=f'radio={r}')
  ys_avg_at_1000.append(y_avg[1000])
  ys_std_at_1000.append(y_std[1000])

ax1.errorbar(r_list, ys_avg_at_1000, fmt='o', yerr=ys_std_at_1000)

# plt.legend(loc='best', fontsize=18)
# plt.yscale("log")
plt.grid()
plt.show()
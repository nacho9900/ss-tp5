import matplotlib.pyplot as plt
from statistics import stdev

savefile_name = ''  # if name is empty, script will show and not save the graph
# savefile_name = 'ej_a/particlesOverTime_100_caudal.png'
list_of_times = []
for i in range(10):
    with open(f'ej_a/particlesOverTime_N_200_opening_1_2_dt_1e-4_seed_{i}.csv') as f:
        lines = f.readlines()
        list_of_times.append([float(line.split(' ')[0]) for line in lines])

def avg(l):
    if len(l) == 0:
        return 0
    return sum(l)/len(l)

# Init graph
fig = plt.figure(figsize=(15,10))
ax1 = fig.add_subplot(111)
ax1.set_xlabel('Tiempo (s)', fontsize=27)
ax1.set_ylabel('Particulas que escaparon', fontsize=27)
ax1.tick_params(axis='both', which='major', labelsize=20, width=2.5, length=10)


# Set up data
x = [avg(times) for times in zip(*list_of_times)]
xerr = [stdev(times) for times in zip(*list_of_times)]
y = [i for i in range(200)]

i1 = int(len(x) * 0.4)
i2 = int(len(x) * 0.8)
caudal = (y[i2] - y[i1]) / (x[i2] - x[i1])
print('Caudal (pendiente estable):', caudal)

# Plot
ax1.errorbar(x, y, xerr=xerr, alpha=0.4)
ax1.plot([x[i1], x[i2]], [y[i1], y[i2]], alpha=0.8, color="black", label=f'Recta estable (caudal = {round(caudal, 4)})')
fig1=plt.gcf()
ax1.legend(fontsize=27)
ax1.grid()

if savefile_name != '':
    plt.savefig(savefile_name)
else:
    plt.show()

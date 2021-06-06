import matplotlib.pyplot as plt
from statistics import stdev

savefile_name = ''  # if name is empty, script will show and not save the graph
# savefile_name = 'ej_a/particlesOverTime_100_descarga.png'
list_of_times = []
for i in range(10):
    with open(f'ej_a/particlesOverTime_N_200_opening_1_2_dt_1e-2_seed_{i}.csv') as f:
        lines = f.readlines()
        list_of_times.append([float(line.split(' ')[0]) for line in lines])

def avg(l):
    if len(l) == 0:
        return 0
    return sum(l)/len(l)

# Set up data
x = [i for i in range(200)]
y = [avg(times) for times in zip(*list_of_times)]
y_err = [stdev(times) for times in zip(*list_of_times)]

i1 = int(len(x) * 0.4)
i2 = int(len(x) * 0.6)
caudal = (y[i2] - y[i1]) / (x[i2] - x[i1])

# Set up graph
fig = plt.figure(figsize=(15,10))
ax1 = fig.add_subplot(111)
ax1.set_xlabel('Particulas que escaparon', fontsize=27)
ax1.set_ylabel('Tiempo (s)', fontsize=27)
ax1.tick_params(axis='both', which='major', labelsize=20, width=2.5, length=10)
# ax1.errorbar(systems[0]['time'], avg_amounts, yerr=stdev_amounts)
ax1.errorbar(x, y, yerr=y_err)
# ax1.set_aspect( 1 )
# plt.xlim([0, 6])
# plt.ylim([0, 6])
fig1=plt.gcf()

if savefile_name != '':
    plt.savefig(savefile_name)
else:
    plt.show()

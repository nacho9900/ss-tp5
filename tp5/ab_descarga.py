import matplotlib.pyplot as plt
from statistics import stdev

savefile_name = ''  # if name is empty, script will show and not save the graph
# savefile_name = 'ej_a/particlesOverTime_100_descarga.png'
list_of_times = []
for i in range(100):
    with open(f'ej_a/particlesOverTime_N_200_opening_1_2_dt_1e-2_seed_{i}.csv') as f:
        lines = f.readlines()
        list_of_times.append([float(line.split(' ')[0]) for line in lines])

def avg(l):
    if len(l) == 0:
        return 0
    return sum(l)/len(l)

# Set up data
amounts = [i for i in range(200)]
avg_times = [avg(times) for times in zip(*list_of_times)]
stdev_times = [stdev(times) for times in zip(*list_of_times)]

# Set up graph
fig = plt.figure(figsize=(15,10))
ax1 = fig.add_subplot(111)
ax1.set_xlabel('Particulas que escaparon', fontsize=27)
ax1.set_ylabel('Tiempo (s)', fontsize=27)
ax1.tick_params(axis='both', which='major', labelsize=20, width=2.5, length=10)
# ax1.errorbar(systems[0]['time'], avg_amounts, yerr=stdev_amounts)
ax1.errorbar(amounts, avg_times, yerr=stdev_times)
# ax1.set_aspect( 1 )
# plt.xlim([0, 6])
# plt.ylim([0, 6])
fig1=plt.gcf()

if savefile_name != '':
    plt.savefig(savefile_name)
else:
    plt.show()

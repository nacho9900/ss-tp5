import matplotlib.pyplot as plt
from statistics import stdev

def d_string(d):
    return str(d).replace('.', '_')

savefile_name = ''  # if name is empty, script will show and not save the graph
# savefile_name = 'ej_a/particlesOverTime_100_caudal.png'

def avg(l):
    if len(l) == 0:
        return 0
    return sum(l)/len(l)


def graph_evolution(N, d):
    list_of_times = []
    
    for i in range(10):
        # with open(f'ej_a/particlesOverTime_N_200_opening_1_2_dt_1e-2_seed_{i}.csv') as f:
        with open(f'ej_c/flowPerOpening_N_{N}_opening_{d_string(d)}_dt_1e-2_seed_{i}.csv') as f:
            lines = f.readlines()
            list_of_times.append([float(line.split(' ')[0]) for line in lines])

    # Set up data
    x = [avg(times) for times in zip(*list_of_times)]
    # x_err = [stdev(times) for times in zip(*list_of_times)]
    y = [i for i in range(200)]
    interval = 10
    caudalesLeft = [(interval) / (x[i+interval] - x[i]) for i in range(len(y)-interval)]
    ax1.errorbar(y[:-interval], caudalesLeft, alpha=0.4, label=d)
    

# Init graph
fig = plt.figure(figsize=(15,10))
ax1 = fig.add_subplot(111)
ax1.set_xlabel('Tiempo (s)', fontsize=27)
ax1.set_ylabel('Particulas que escaparon', fontsize=27)
ax1.tick_params(axis='both', which='major', labelsize=20, width=2.5, length=10)


Nds = [(380, 3.0)]
for N, d in Nds:
    graph_evolution(N, d)

fig1=plt.gcf()
box = ax1.get_position()
ax1.set_position([box.x0, box.y0 + box.height * 0.1,
                 box.width * 0.9, box.height * 0.9])
# get handles
handles, labels = ax1.get_legend_handles_labels()
# remove the errorbars
handles = [h[0] for h in handles]
# use them in the legend
ax1.legend(handles, labels, loc='upper center',numpoints=1, bbox_to_anchor=(1.13,1), fontsize=27)

# Descomentar lo de abajo para ver la pendiente
if savefile_name != '':
    plt.savefig(savefile_name)
else:
    plt.show()

import axios from "../../axios";

export default {
    state: {
        workers: []
    },
    mutations: {
        setAllLoading(state, flag) {
            state.workers.forEach(workers => workers.loading = flag);
        },
        updateWorkers(state, workers) {
            state.workers = workers;
        },
        addWorker(state, worker) {
            state.workers.unshift(worker);
        },
        updateWorker(state, worker) {
            let candidate = state.workers.find(item => item.id === worker.id);
            candidate.alias = worker.alias;
            candidate.version = worker.version;
            candidate.id = worker.id;
            candidate.connectionState = worker.connectionState;
            candidate.loading = worker.loading;
        },
        deleteWorker(state, id) {
            state.workers = state.workers.filter(element => element.id !== id);
        }
    },
    actions: {
        refetchWorkers(ctx) {
            ctx.commit('setAllLoading', true);
            setTimeout(() => ctx.dispatch('fetchWorkers'), 1000);
        },
        fetchWorker(ctx, id) {
            return new Promise((resolve, reject) => {
                axios.get(`/worker/${id}`)
                    .then(response => {
                        if (response) {
                            response.data.loading = false;
                            ctx.commit('updateWorker', response.data);
                            resolve(response.data);
                        }
                    }).catch(reason => reject(reason))
            })
        },
        fetchWorkers(ctx) {
            return new Promise((resolve, reject) => {
                axios.get("/worker")
                    .then(response => {
                        if (response) {
                            let workers = response.data.reverse();
                            workers.forEach(workers => workers.loading = false);
                            ctx.commit("updateWorkers", workers);
                            resolve(workers)
                        }
                    }).catch(reason => reject(reason))
            })
        },
        addWorker(ctx, worker) {
            return new Promise((resolve, reject) => {
                axios.put("/worker", worker)
                    .then(response => {
                        if (response) {
                            response.data.loading = true;
                            const key = response.data.key;
                            delete response.data.key;
                            ctx.commit("addWorker", response.data);
                            let result = null;
                            setTimeout(() => result = ctx.dispatch('fetchWorker', response.data.id), 1000);
                            resolve({...result, key})
                        }
                    }).catch(reason => reject(reason))
            })
        },
        updateWorker(ctx, worker) {
            const {alias, version} = worker;
            return new Promise((resolve, reject) => {
                axios.patch(`/worker/${worker.id}`, {alias, version})
                    .then(response => {
                        if (response) {
                            response.data.loading = true;
                            ctx.commit("updateWorker", response.data);
                            let result = null;
                            setTimeout(() => result = ctx.dispatch('fetchWorker', worker.id), 1000);
                            resolve(result)
                        }
                    }).catch(reason => reject(reason))
            })
        },
        resetKey(ctx, id) {
            return new Promise((resolve, reject) => {
                axios.post(`/worker/${id}/resetKey`)
                    .then(response => {
                        resolve(response.data)
                    }).catch(reason => reject(reason))
            })
        },
        deleteWorker(ctx, id) {
            axios.delete(`/worker/${id}`)
                .then((response) => {
                    if (response)
                        ctx.commit("deleteWorker", id);
                })
        }
    },
    getters: {
        getWorkers(state) {
            return state.workers;
        }
    }
}
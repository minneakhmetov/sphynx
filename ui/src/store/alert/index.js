const alertConfigs = {
    alertTimeout: 3000
};
export default {
    state: {
        queue: []
    },
    mutations: {
        addToQueue(state, alert) {
            state.queue.push(alert);
        },
        deleteFromQueue(state) {
            state.queue.shift();
        }
    },
    actions: {
        async showMessage(ctx, message) {
            const alert = {
                enabled: false,
                message: message
            };
            ctx.commit('addToQueue', alert);
            setTimeout(() => alert.enabled = true, 100);
            setTimeout(() => {
                alert.enabled = false;
                setTimeout(() => ctx.commit('deleteFromQueue'), 100);
            }, alertConfigs.alertTimeout);
        }
    },
    getters: {
        getAlertQueue(state) {
            return state.queue;
        }
    }
}
import axios from "../../axios";
import dbManager from "../../constants/database";

export default {
    state: {
        databases: [],
        subscription: null
    },
    mutations: {
        updateDatabases(state, databases) {
            state.databases = databases;
        },
        updateDatabase(state, database) {
            database.icon = dbManager.findDbByType(database.type).icon;
            database.configs = JSON.parse(database.configs);
            let candidate = state.databases.find(item => item.id === database.id);
            candidate.id = database.id;
            candidate.alias = database.alias;
            candidate.configs = database.configs;
            candidate.userId = database.userId;
            candidate.workerId = database.workerId;
            candidate.type = database.type;
            candidate.icon = database.icon;
        },
        updateState(state, database) {
            let candidate = state.databases.find(item => item.id === database.id);
            candidate.connectionState = database.connectionState;
            candidate.loading = false;
        },
        addDatabase(state, database) {
            state.databases.unshift(database);
        },
        addSubscription(state, subscription) {
            state.subscription = subscription;
        }
    },
    actions: {
        fetchDatabases(ctx) {
            return new Promise((resolve, reject) => {
                axios.get("/database")
                    .then(response => {
                        if (response) {
                            let databases = response.data;
                            databases.forEach(database => {
                                database.icon = dbManager.findDbByType(database.type).icon;
                                database.loading = true;
                                database.configs = JSON.parse(database.configs);
                            });
                            //  this.databasesLoading = false;
                            databases = databases.reverse();
                            ctx.commit('updateDatabases', databases);
                           // ctx.dispatch('fetchStatus');
                            resolve(databases);
                        }
                    }).catch(reason => reject(reason))
            });
        }
    },
    getters: {
        getDatabases(state) {
            return state.databases;
        },
        getSubscription(store) {
            return store.subscription;
        }
    }
}
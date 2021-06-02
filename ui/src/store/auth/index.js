import axios from "../../axios";
export default {
    state: {
        auth: getUserFromLocalStorage()
    },
    mutations: {
        changeAuth(state, auth) {
            state.auth = auth;
        },
        saveUserAuth(state, data) {
            localStorage.token = data.token;
            localStorage.userId = data.claims.id;
            localStorage.email = data.claims.email;
            localStorage.login = data.claims.login;
            localStorage.role = data.claims.role;
            state.auth = data;
        },

    },
    actions: {
        removeUserAuth(ctx) {
            localStorage.removeItem('token');
            localStorage.removeItem('userId');
            localStorage.removeItem('email');
            localStorage.removeItem('login');
            localStorage.removeItem('role');
            ctx.commit('changeAuth', null);
        },
        login(ctx, body) {
            return axios.post("/auth/login", body)
                .then(response => {
                    if (response) {
                        ctx.commit('saveUserAuth', response.data);
                        ctx.dispatch('fetchWorkers');
                    }
                })
        },
        logout(ctx) {
            return axios.post("/auth/logout")
                .then(() => {
                    ctx.dispatch('removeUserAuth');
                    ctx.commit('updateWorkers', null);
                })
        },
        getUserAuth(ctx) {
            const auth = this.state.auth;
            if (auth) {
                return auth
            } else {
                const userAuth = getUserFromLocalStorage();
                if (userAuth) {
                    ctx.commit("changeAuth", userAuth);
                    return userAuth;
                } else return null;
            }
        }
    },
    getters: {
        getAuth(state) {
            return state.auth;
        },
        getAuthHeaders(state) {
            if (!state.auth) {
                const token = localStorage.token;
                if (token) {
                    return {
                        'Token': token
                    }
                }
                return null;
            } return {
                'Token': state.auth.token
            }
        }
    }
}

function getUserFromLocalStorage() {
    const token = localStorage.token;
    const id = localStorage.userId;
    const email = localStorage.email;
    const login = localStorage.login;
    const role = localStorage.role;
    if (id && email && login && role && token) {
        return {
            token,
            claims: {
                id, email, login, role
            }
        };
    } else return null;
}
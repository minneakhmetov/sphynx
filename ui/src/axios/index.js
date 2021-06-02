import store from "../store";
import router from "../router";
import Axios from "axios";
import constants from "../constants";

const alertMessage = message => store.dispatch("showMessage", message);

const axios = Axios.create({
    baseURL: constants.baseUrl
});

axios.interceptors.request.use(request => {
    const headers = store.getters['getAuthHeaders'];
    if (headers) {
        request.headers.common = {...request.headers.common, ...headers};
    }
    return request;
});

axios.interceptors.response.use((response) => {
        if (response.data.error) {
            handleKnownError(response.data.error)
        } else {
            return response;
        }
    }, (error) => {
        const response = error.response;
        if (response) {
            if (response.data.error) {
                handleKnownError(response.data.error);
            } else {
                alertMessage(error);
                throw error;
            }
        } else {
            alertMessage(error.message);
            throw error;
        }
    }
);

function handleKnownError(errors) {
    if (Array.isArray(errors)) {
        errors.forEach(error => {
            if (error.code) {
                if (error.code === "000003" || error.code === "000004") {
                    alertMessage(error.code + ": " + error.description);
                    store.dispatch("removeUserAuth");
                    router.push("/login");
                } else {
                    alertMessage(error.code + ": " + error.description);
                }
            } else {
                alertMessage(error);
            }
        });
    } else alertMessage('Backend: ' + errors)
}

export default axios;
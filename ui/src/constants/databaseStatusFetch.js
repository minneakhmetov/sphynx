import stompClient from "../socket";
import axios from "../axios";
import store from '../store'

const sub = store.getters['getSubscription'];

export default function fetchStatus() {
    if (!sub) {
        let subscription = stompClient.subscribe(`/databases`, (updated) => {
            store.commit('updateState', JSON.parse(updated.body));
        }, {'id-sub': 99});
        store.commit('addSubscription', subscription);
    }
    axios.post('/database/health');
}
import SockJS from "sockjs-client";
import Stomp from "stompjs";
import constants from "../constants";
import store from "../store";


// let socket = new SockJS(constants.baseUrl + "/ws?token=" + token, null, {transports:'websocket'});
const headers = {};

class SocketClient {

    connected = false;
    stompClient = null;
    auth = null;
    token = null;
    username = null;

    connect(auth) {
        return new Promise((resolve, reject) => {
            if (auth) {
                this.auth = auth;
            } else this.auth = store.getters['getAuth'];
            this.token = this.auth ? this.auth.token : null;
            this.username = this.auth ? this.auth.claims.login : null;
            const socket = new SockJS(constants.baseUrl + "/ws?token=" + this.token);
            this.stompClient = Stomp.over(socket);
            this.stompClient.connect(headers, () => {
                this.connected = true;
                resolve();
            }, e => {
                reject(e)
            });
        });
    }

    sendMessage(topic, message) {
        if (this.connected) {
            this.stompClient.send(topic, headers, message);
        } else {
            this.connect()
                .then(() => this.stompClient.send(topic, headers, message));
        }
    }

    async subscribe(topic, callback, headers) {
        // if (!auth){
        //     throw new Error("Auth error")
        // }
        if(!this.connected){
            await this.connect();
        }
        return this.stompClient.subscribe(`/user/${this.username}${topic}`, callback, headers);
    }

    disconnect() {
        return new Promise((resolve, reject) => {
            if(this.connected) {
                this.stompClient.disconnect(() => {
                    this.connected = false;
                    resolve();
                })
            } else {
                reject(new Error("Web Socket is not connected"));
            }
        })
    }
}

export default new SocketClient();
<template>
    <v-app>
        <div class="above">
            <AlertView v-for="(item, i) in getAlertQueue" :alert="item" :key="i"/>
        </div>
        <v-container fluid v-if="this.getAuth">
            <v-row>
                <v-col cols="2" class="pl-0">
                    <MenuView/>
                </v-col>
                <v-col cols="10">
                    <HeaderView/>
                    <router-view/>
                </v-col>
            </v-row>
        </v-container>
        <router-view v-else></router-view>
    </v-app>
</template>

<script>
    import AlertView from './components/AlertView.vue';
    import HeaderView from './components/HeaderView.vue';
    import MenuView from './components/MenuView.vue';
    import {mapGetters, mapActions} from 'vuex';
    import stompClient from "./socket";

    export default {
        name: 'App',
        data: () => ({

        }),
        components: {
            AlertView, HeaderView, MenuView
        },
        computed: {
            ...mapGetters(["getAuth"]),
            ...mapGetters(["getAlertQueue"]),
        },
        methods: {
            ...mapActions(['fetchWorkers']),
            fetchAndConnect(){
                let auth = this.getAuth;
                if (auth) {
                    this.fetchWorkers();
                    if (auth.token)
                        stompClient.connect(auth);
                    // stompClient.connect({'Token': auth.token}, () => {});
                }
            }
        },
        mounted() {
            this.fetchAndConnect();
        },
        watch: {
            getAuth(newValue, oldValue){
                if (newValue && !oldValue)
                    this.fetchAndConnect();
                // if (!newValue && oldValue){
                //     stompClient.disconnect();
                // }
            }
        }

    };
</script>

<style>
    .above {
        position: absolute;
        z-index: 1;
        width: 400px;
        right: 0;
        left: 0;
        margin: 1% auto auto;
    }
</style>

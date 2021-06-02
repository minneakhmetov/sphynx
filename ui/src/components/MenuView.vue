<template>
    <div>
        <v-img
                :src="require('../assets/logo.svg')"
                height="3rem"
                contain
                class="clickable mb-3 mt-3"
                @click="redirect('/main')"
        />
        <v-card
                tile
                elevation="0"
                color="lightgray"
        >
            <v-list>
                <v-list-item-group
                        v-model="selectedItem"
                        color="amber darken-2"
                        mandatory
                >
                    <v-list-item
                            v-for="(item, i) in this.getItems.filter(item => item.enabled)"
                            :key="i"
                            @click="redirect(item.link)"
                    >
                        <v-list-item-icon>
                            <v-icon v-text="item.icon"></v-icon>
                        </v-list-item-icon>
                        <v-list-item-content>
                            <v-list-item-title v-text="item.text"></v-list-item-title>
                        </v-list-item-content>
                    </v-list-item>
                </v-list-item-group>
            </v-list>
        </v-card>
    </div>
</template>

<script>
    import router from "../router";
    import {mapGetters} from "vuex";

    export default {
        name: "MenuView",
        data: () => ({
            selectedItem: null,
        }),
        mounted() {
          this.selectedItem = this.getCurrentTab;
        },
        computed: {
            ...mapGetters(['getAuth']),
            getItems() {
                const userAuth = this.getAuth;
                const role = userAuth ? userAuth.claims.role : null;
                return [{text: 'Databases', icon: 'mdi-database', enabled: true, link: "/database"},
                    {text: 'Tests', icon: 'mdi-dip-switch', enabled: true, link: "/tests"},
                    {text: 'SQL Lab', icon: 'mdi-ab-testing', enabled: true, link: "/sqllab"},
               //     {text: 'Webhooks', icon: 'mdi-alarm-light-outline', enabled: true, link: "/webhooks"},
                    {
                        text: 'User Management',
                        icon: 'mdi-account-multiple',
                        enabled: role === 'ROLE_ADMIN',
                        link: "/users"
                    },
                    {
                        text: 'Worker Management',
                        icon: 'mdi-server-network',
                        enabled: role === 'ROLE_ADMIN',
                        link: "/workers"
                    },
                    {text: 'Cluster Settings', icon: 'mdi-cog', enabled: role === 'ROLE_ADMIN', link: "/settings"}];
            },
            getCurrentTab() {
                let items = this.getItems;
                if (router.history.pending) {
                    for (let i = 0; i < items.length; i++) {
                        if (router.history.pending.path === items[i].link) {
                            return i;
                        }
                    }
                }
                return null;
            }
        },
        methods: {
            redirect(link) {
                router.push(link).catch(() => {});
            }
        }
    }
</script>

<style scoped>


</style>
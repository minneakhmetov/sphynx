<template>
    <v-card flat>
        <v-container fluid>
            <v-row class="child-flex">
                <div>
                    <v-toolbar dense elevation="0" style="display: flex; align-items: center; justify-content: flex-end">
                        <v-toolbar-title icon style="font-size: medium">
                            {{getAuth.claims.login}}
                        </v-toolbar-title>
                        <v-menu
                                bottom
                                left
                        >
                            <template v-slot:activator="{ on, attrs }">
                                <v-btn
                                        icon
                                        v-bind="attrs"
                                        v-on="on"
                                >
                                    <v-icon>mdi-dots-vertical</v-icon>
                                </v-btn>
                            </template>
                            <v-list-item-group>
                                <v-list>
                                    <v-list-item
                                            v-for="(item, i) in getItems"
                                            :key="i"
                                    >
                                        <v-list-item-title @click="item.link">{{ item.title }}</v-list-item-title>
                                    </v-list-item>
                                </v-list>
                            </v-list-item-group>
                        </v-menu>
                    </v-toolbar>
                </div>
            </v-row>
        </v-container>
    </v-card>
</template>

<script>
    import router from "../router";
    import {mapActions, mapGetters} from 'vuex';
    import stompClient from "../socket";

    export default {
        name: "HeaderView",
        computed: {
            ...mapGetters(['getAuth']),
            getItems() {
                return [
                    {
                        title: 'Profile',
                        link: () => {
                            console.log("Profile!")
                        }
                    },
                    {
                        title: 'Logout',
                        link: () => {
                            this.logout().then(() => {
                                router.push("/login");
                                stompClient.disconnect({}, () => {});
                            });
                        }
                    },
                ]
            }
        },
        methods: {
            ...mapActions(["logout"])
        }
    }
</script>

<style scoped>
    .clickable {
        cursor: pointer;
    }
</style>
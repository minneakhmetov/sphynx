<template>
    <v-container fluid>
        <v-row>
            <v-dialog
                    max-width="800"
                    v-model="detailedDialog"
            >
                <v-card v-if="detailed">
                    <v-toolbar
                            color="amber darken-3"
                            dark
                    >{{detailed.name}}
                    </v-toolbar>
                    <v-card-text class="pa-0">
                        <v-data-table
                                :headers="getHeaders(detailed)"
                                :items="detailed.result"
                                :items-per-page="5"
                                class="elevation-1 "
                        ></v-data-table>
                    </v-card-text>
                    <v-card-actions class="justify-end">
                        <v-btn
                                text
                                @click="closeDetailedDialog"
                        >Close
                        </v-btn>
                    </v-card-actions>
                </v-card>
            </v-dialog>
        </v-row>
        <v-row>
            <v-dialog
                    v-model="deleteDialog"
                    max-width="290"
            >
                <v-card>
                    <v-card-title class="headline">
                        Are you sure to delete {{deleteCandidate ? deleteCandidate.name : null}}?
                    </v-card-title>

                    <v-card-text>
                        This action cannot be reverted. All attached test would be also deleted
                    </v-card-text>

                    <v-card-actions>
                        <v-spacer></v-spacer>

                        <v-btn
                                color="red darken-1"
                                text
                                @click="deleteItem(deleteCandidate)"
                        >
                            Continue
                        </v-btn>

                        <v-btn
                                color="blue darken-1"
                                text
                                @click="setDelete(null, false)"
                        >
                            Cancel
                        </v-btn>
                    </v-card-actions>
                </v-card>
            </v-dialog>
        </v-row>
        <v-row>
            <v-dialog
                    v-model="dialog"
                    max-width="400"
            >
                <v-card>
                    <v-card-title class="headline">
                        Save
                    </v-card-title>

                    <v-card-text>
                        <v-text-field
                                label="Name"
                                :rules="rules"
                                v-model="model.name"
                                hide-details="auto"
                        ></v-text-field>
                    </v-card-text>

                    <v-card-actions>
                        <v-spacer></v-spacer>

                        <v-btn
                                color="blue darken-1"
                                text
                                @click="cancel"
                        >
                            Cancel
                        </v-btn>

                        <v-btn
                                color="amber darken-3"
                                text
                                @click="save"
                        >
                            Save
                        </v-btn>
                    </v-card-actions>
                </v-card>
            </v-dialog>
        </v-row>
        <!--        <v-row>-->
        <!--            <v-col align="left">-->
        <!--                <h2>-->
        <!--                    SQL Lab-->
        <!--                </h2>-->
        <!--            </v-col>-->
        <!--        </v-row>-->
        <v-form ref="validate">
            <v-row>
                <v-col cols="3" class="pb-0">
                    <v-select
                            v-model="select.databaseId"
                            :items="getDatabasesIds"
                            label="Select database"
                            required
                            :rules="[(v) => !!v || 'Required']"
                            class="mt-0"
                    >
                        <template slot="no-data">
                            <div class="d-flex align-center justify-center pa-4 mx-auto">
                                <v-progress-circular
                                        v-if="databasesLoading"
                                        indeterminate
                                        color="amber darken-3"
                                ></v-progress-circular>
                                <div class="text-subtitle-1" v-else>
                                    There are no created databases
                                </div>
                            </div>
                        </template>
                        <template v-slot:selection="{ item }">
                            <img style="max-width: 1.7rem; margin-right: 3%"
                                 :src="findDbByType(getDatabaseById(item).type).icon">
                            <div class="text-subtitle-1">
                                {{ getDatabaseById(item).alias }}
                            </div>
                        </template>
                        <template v-slot:item="{ item }">
                            <img style="max-width: 1.7rem; margin-right: 3%"
                                 :src="findDbByType(getDatabaseById(item).type).icon">
                            <div class="text-subtitle-1" style="margin-right: 3%">
                                {{ getDatabaseById(item).alias }}
                            </div>
                            <div style="display: flex; align-items: center; justify-content: flex-end">
                                <div class="font-weight-medium"
                                     :style="{color: connectionState[getDatabaseById(item).connectionState].color}">
                                    {{getDatabaseById(item).connectionState}}
                                </div>
                            </div>
                        </template>
                    </v-select>
                </v-col>
            </v-row>
            <v-row>
                <v-col class="pt-0">
                    <v-textarea
                            label="SQL query"
                            auto-grow
                            outlined
                            rows="4"
                            :rules="rules"
                            v-model="select.sql"
                    ></v-textarea>
                </v-col>
            </v-row>
        </v-form>
        <v-row>
            <v-col class="pt-0">
                <v-btn
                        :loading="processing"
                        :disabled="processing && subscription !== null"
                        color="amber darken-3"
                        @click="query"
                >
                    Query
                </v-btn>
            </v-col>
            <v-col align="right" v-if="this.model.timeEnd && this.model.timeStart">
                <h4>Elapsed time: {{elapsedTime}}</h4>
            </v-col>
        </v-row>
        <v-row v-if="model.message">
            <v-col>
                <v-alert
                        prominent
                        type="error"
                >
                    {{model.message}}
                </v-alert>
            </v-col>
        </v-row>
        <div v-else-if="model.result">
            <v-row>
                <v-col>
                    <v-data-table
                            :headers="getHeadersForCurrentModel"
                            :items="model.result"
                            :items-per-page="5"
                            class="elevation-1"
                    ></v-data-table>
                </v-col>
            </v-row>
            <v-row>
                <v-col class="pt-0">
                    <v-btn
                            color="secondary"
                            @click="dialog = true"
                    >
                        Save query
                    </v-btn>
                </v-col>
            </v-row>
        </div>
        <v-row>
            <v-col>
                <v-card
                        class="mx-auto"
                        tile
                        elevation="0"

                >
                    <div v-if="savedQueries.length">
                        <v-row align="center" justify="center" style="height: 4.8rem">
                            <v-col cols="1">
                                <div align="center">
                                    <p class="mb-0">Number</p>
                                </div>
                            </v-col>
                            <v-col cols="2">
                                <div align="center">
                                    <p class="mb-0">
                                        Name
                                    </p>
                                </div>
                            </v-col>
                            <v-col cols="2">
                                <div align="center">
                                    <p class="mb-0">
                                        Database
                                    </p>
                                </div>
                            </v-col>
                            <v-col cols="4">
                                <div align="center">
                                    <p class="mb-0">
                                        SQL
                                    </p>
                                </div>
                            </v-col>
                            <v-col cols="1">
                                <div align="center">
                                    <p class="mb-0">
                                        Elapsed time
                                    </p>
                                </div>
                            </v-col>
                            <v-col cols="1">
                            </v-col>
                        </v-row>
                        <v-list>
                            <v-list-item-group v-model="selected">
                                <v-list-item class="mb-4 elevation-3"
                                             v-for="(query, i) in savedQueries"
                                             :key="i"
                                             @click="getDetails(query)"
                                >
                                    <v-list-item-content>
                                        <v-row align="center" justify="center" style="height: 4.8rem">
                                            <v-col cols="1" class="">
                                                <div align="left">
                                                    <h2>
                                                        {{`#${savedQueries.length - i}.`}}
                                                    </h2>
                                                </div>
                                            </v-col>
                                            <v-col cols="2">
                                                <div align="center">
                                                    <p class="mb-0">
                                                        {{query.name}}
                                                    </p>
                                                </div>
                                            </v-col>
                                            <v-col cols="2">
                                                <div style="display: flex; align-items: center; justify-content: flex-start">
                                                    <img style="max-width: 1.7rem; margin-right: 3%"
                                                         :src="findDbByType(getDatabaseById(query.databaseId).type).icon">
                                                    {{ getDatabaseById(query.databaseId).alias }}
                                                </div>
                                            </v-col>
                                            <v-col cols="4">
                                                <div align="center">
                                                    <p class="mb-0">
                                                        {{query.sql}}
                                                    </p>
                                                </div>
                                            </v-col>
                                            <v-col cols="1">
                                                <div align="center">
                                                    <p class="mb-0">
                                                        {{getDistance(query.timeStart, query.timeEnd)}}
                                                    </p>
                                                </div>
                                            </v-col>
                                            <v-col cols="1" align="right">
                                                <v-btn
                                                        color="red"
                                                        dark
                                                        @click.stop="setDelete(query, true)"
                                                >
                                                    <v-icon>mdi-delete</v-icon>
                                                </v-btn>
                                            </v-col>
                                        </v-row>
                                    </v-list-item-content>
                                </v-list-item>
                            </v-list-item-group>
                        </v-list>
                    </div>
                    <div class="d-flex align-center justify-center pa-4 mx-auto" v-else-if="loading">
                        <v-progress-circular
                                indeterminate
                                color="amber darken-3"
                        ></v-progress-circular>
                    </div>
                    <div class="d-flex align-center justify-center pa-4 mx-auto" v-else>
                        <div class="text-subtitle-1">
                            There are no one saved query now.
                        </div>
                    </div>
                </v-card>
            </v-col>
        </v-row>

    </v-container>
</template>

<script>
    import axios from "../axios";
    import stompClient from "../socket";
    import {mapActions, mapGetters} from "vuex";
    import dbManager from "../constants/database";
    import databaseStatusFetch from "../constants/databaseStatusFetch";
    import connectionState from "../constants/connectionstate";

    export default {
        name: "SQLLabView",
        data: () => ({
            rules: [
                value => !!value || 'Required.',
                value => (value && value.length >= 3) || 'Min 3 characters',
            ],
            savedQueries: [],
            loading: false,
            subscription: null,
            processing: false,
            select: {},
            model: {},
            databasesLoading: false,
            dialog: false,
            detailed: null,
            detailedDialog: false,
            deleteDialog: false,
            deleteCandidate: null,
            selected: undefined,
            connectionState
        }),
        beforeMount() {
            this.databaseFetch();
        },
        mounted() {
            this.getSavedQueries();
            setTimeout(() => this.subscribe(), 1000);
        },
        destroyed() {
            if (this.subscription) {
                this.subscription.then(sub => sub.unsubscribe());
            }
        },
        methods: {
            ...mapActions(["fetchDatabases"]),
            getSavedQueries() {
                this.loading = true;
                axios.get("/sqllab").then(response => {
                    if (response.data) {
                        this.savedQueries = response.data.reverse();
                        this.loading = false;
                    }
                })
            },
            subscribe() {
                this.subscription = stompClient.subscribe(`/sqllab`, (updated) => {
                    let body = JSON.parse(updated.body);
                    body.result = JSON.parse(body.result);
                    this.model = body;
                    this.processing = false;
                }, {'id-sub': 98});
            },
            query() {
                if (this.$refs.validate.validate()) {
                    this.processing = true;
                    this.model = {};
                    axios.post('/sqllab/query', this.select).then(response => {
                        // eslint-disable-next-line no-empty
                        if (response.data) {

                        }
                    })
                }
            },
            getDatabaseById(id) {
                return this.getDatabases.find(database => database.id === id);
            },
            findDbByType(type) {
                return dbManager.findDbByType(type);
            },
            save() {
                axios.post('/sqllab/save/' + this.model.id, {}, {params: {name: this.model.name}}).then(response => {
                    if (response.status === 200) {
                        this.savedQueries.unshift(this.model);
                        this.dialog = false;
                    }
                })
            },
            databaseFetch() {
                this.databasesLoading = true;
                this.fetchDatabases().then(() => {
                    databaseStatusFetch();
                    this.databasesLoading = false;
                })
            },
            getDate(array) {
                return new Date(array[0], array[1] - 1, array[2], array[3], array[4], array[5], array[6] / 100000);
            },
            cancel() {
                this.dialog = false;
                this.model.name = null
            },
            getDistance(timeStart, timeEnd) {
                let distance = this.getDate(timeEnd).getTime() - this.getDate(timeStart).getTime();
                const hours = Math.floor(distance / 3600000);
                distance -= hours * 3600000;
                const minutes = Math.floor(distance / 60000);
                distance -= minutes * 60000;
                const seconds = Math.floor(distance / 1000);
                return `${hours}:${('0' + minutes).slice(-2)}:${('0' + seconds).slice(-2)}`;
            },
            deleteItem(model) {
                axios.delete('/sqllab/' + model.id).then(response => {
                    if (response.status === 200) {
                        this.savedQueries = this.savedQueries.filter(query => query.id !== model.id);
                        this.deleteDialog = false;
                    }
                })
            },
            setDelete(model, deleteDialog) {
                this.deleteCandidate = model;
                this.deleteDialog = deleteDialog;
            },
            getDetails(model) {
                axios.get('/sqllab/' + model.id).then(response => {
                    if (response.data) {
                        response.data.result = JSON.parse(response.data.result);
                        this.detailed = response.data;
                        this.detailedDialog = true;
                    }
                })
            },
            getHeaders(obj) {
                let arr = [];
                if (obj.result.length > 0) {
                    Object.keys(obj.result[0]).forEach(obj => arr.push({text: obj, value: obj}));
                }
                return arr;
            },
            closeDetailedDialog() {
                this.detailedDialog = false;
                this.detailed = null;
            }
        },
        computed: {
            ...mapGetters(["getDatabases"]),
            getDatabasesIds() {
                return this.getDatabases.map(database => database.id);
            },
            getHeadersForCurrentModel() {
                return this.getHeaders(this.model);
            },
            elapsedTime() {
                return this.getDistance(this.model.timeStart, this.model.timeEnd);
            }
        }
    }
</script>

<style scoped>
    .query-list {
        cursor: pointer;
    }

    .query-list:hover {
        cursor: pointer;
        background-color: lightgrey;
    }
</style>
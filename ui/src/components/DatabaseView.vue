<template>
    <v-container>

        <v-row justify="center">
            <v-dialog
                    v-model="deleteDialog"
                    max-width="290"
            >
                <v-card>
                    <v-card-title class="headline">
                        Are you sure to delete {{deleteCandidate ? deleteCandidate.alias : null}}?
                    </v-card-title>

                    <v-card-text>
                        This action cannot be reverted. All attached test would be also deleted
                    </v-card-text>

                    <v-card-actions>
                        <v-spacer></v-spacer>

                        <v-btn
                                color="red darken-1"
                                text
                                @click="deleteDatabase"
                        >
                            Continue
                        </v-btn>

                        <v-btn
                                color="blue darken-1"
                                text
                                @click="setDeleteDialog(false, null)"
                        >
                            Cancel
                        </v-btn>
                    </v-card-actions>
                </v-card>
            </v-dialog>
        </v-row>
        <v-row>
            <v-col>
                <v-dialog
                        v-model="dialog"
                        persistent
                        max-width="600px"
                >
                    <template v-slot:activator="{ on, attrs }">
                        <v-btn
                                elevation="0"
                                class="sphynx-secondary mt-4"
                                v-bind="attrs"
                                v-on="on"
                        >
                            + Add new database
                        </v-btn>
                    </template>
                    <v-card>
                        <v-card-title>
                            <span class="headline">{{this.update ? 'Update ' + this.dbProps.alias : 'Adding new database'}}</span>
                        </v-card-title>
                        <v-card-text>
                            <v-form ref="form" v-on:submit.prevent="">
                                <v-container>
                                    <v-row>
                                        <v-select
                                                v-model="select"
                                                :items="getDatabaseTypes"
                                                label="Select database type"
                                                required
                                        >
                                            <template v-slot:selection="{ item }">
                                                <img style="max-width: 1.7rem; margin-right: 3%"
                                                     :src="findDbByType(item).icon">
                                                {{ findDbByType(item).name }}
                                            </template>
                                            <template v-slot:item="{ item }">
                                                <img style="max-width: 1.7rem; margin-right: 3%"
                                                     :src="findDbByType(item).icon">
                                                {{ findDbByType(item).name }}
                                            </template>
                                        </v-select>
                                    </v-row>
                                    <v-row >
                                        <v-select
                                                v-model="dbProps.workerId"
                                                :items="workersIds"
                                                label="Select worker"
                                                required
                                                class="pt-0"
                                        >
                                            <template v-slot:selection="{ item }">
                                                <v-icon style="max-width: 1.7rem; margin-right: 3%">
                                                    mdi-server-network
                                                </v-icon>
                                                {{ getWorkerById(item).alias }}
                                            </template>
                                            <template v-slot:item="{ item }">
                                                <v-icon style="max-width: 1.7rem; margin-right: 3%">
                                                    mdi-server-network
                                                </v-icon>
                                                {{ getWorkerById(item).alias }}
                                            </template>
                                        </v-select>
                                    </v-row>
                                    <v-row class="pb-5">
                                        <v-text-field
                                                label="Alias"
                                                :rules="rules"
                                                hide-details="auto"
                                                v-model="dbProps.alias"
                                                text
                                                class="pt-0"
                                        ></v-text-field>
                                    </v-row>
                                    <v-row v-for="(fieldName, i) in Object.keys(fields)" :key="i">
                                        <v-text-field
                                                :label="fieldName"
                                                :rules="fields[fieldName]"
                                                v-model="input[fieldName]"
                                                required
                                        ></v-text-field>
                                    </v-row>
                                </v-container>
                            </v-form>
                        </v-card-text>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn
                                    color="blue darken-1"
                                    text
                                    @click="closeDialog"
                            >
                                Close
                            </v-btn>
                            <v-btn
                                    color="blue darken-1"
                                    text
                                    @click="saveOrUpdate"
                            >
                                Save
                            </v-btn>
                        </v-card-actions>
                    </v-card>
                </v-dialog>
            </v-col>
        </v-row>
        <v-row>
            <v-col>
                <v-card
                        class="mx-auto"
                        tile
                        elevation="0"

                >
                    <div v-if="getDatabases.length">
                        <v-list-item class="mb-4 elevation-3"
                                     v-for="(database, i) in getDatabases"
                                     v-bind:key="i"
                                     >
                            <v-list-item-content>
                                <v-row align="center" justify="center" style="height: 4.8rem">
                                    <v-col cols="1">
                                        <div align="center">
                                            <img
                                                    :src="database.icon" style="max-width: 3rem"
                                            />
                                        </div>
                                    </v-col>
                                    <v-col cols="2" class="pl-0">
                                        <v-list-item-title style="font-size: 140%">{{database.alias}}
                                        </v-list-item-title>
                                    </v-col>
                                    <v-col cols="3">
                                        <v-icon style="max-width: 1.7rem; margin-right: 3%">
                                            mdi-server-network
                                        </v-icon>
                                        {{ getWorkerById(database.workerId).alias }}
                                    </v-col>
                                    <v-col cols="6" class="text-right" style="display: flex; align-items: center; justify-content: flex-end">
                                        <v-progress-circular
                                                v-if="database.loading "
                                                indeterminate
                                                color="amber darken-3"
                                                :size="20"
                                                style="margin-right: 3%"
                                        ></v-progress-circular>
                                        <div v-else class="font-weight-medium" style="margin-right: 3%">
                                            <div :style="{color: connectionState[database.connectionState].color}">
                                                {{database.connectionState}}
                                            </div>
                                        </div>
                                        <!--                                        <p style="font-size: 100%">Successful</p>-->
                                        <v-btn
                                                elevation="0"
                                                class="sphynx-secondary mr-3"
                                                @click.stop="openUpdateDialog(database)"
                                        >
                                            Edit
                                        </v-btn>
                                        <v-btn
                                                color="red"
                                                dark
                                                @click.stop="setDeleteDialog(true, database)"
                                        >
                                            <v-icon>mdi-delete</v-icon>
                                        </v-btn>
                                    </v-col>
                                </v-row>
                            </v-list-item-content>
                        </v-list-item>
                    </div>
                    <div class="d-flex align-center justify-center pa-4 mx-auto" v-else-if="loading">
                        <v-progress-circular
                                indeterminate
                                color="amber darken-3"
                        ></v-progress-circular>
                    </div>
                    <div class="d-flex align-center justify-center pa-4 mx-auto" v-else>
                        <div class="text-subtitle-1">
                            There are no one database now. <a @click="dialog = true">Create the first</a>
                        </div>
                    </div>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
    import '../css/styles.sass'
    import dbManager from '../constants/database';
    import axios from '../axios';
    import {mapGetters, mapActions, mapMutations} from "vuex";
    import connectionState from "../constants/connectionstate";
    import databaseStatusFetch from "../constants/databaseStatusFetch";

    export default {
        name: "DatabaseView",
        data: () => ({
            loading: true,
            dialog: false,
            fields: [],
            select: null,
            input: {},
            update: false,
            deleteDialog: false,
            deleteCandidate: null,
            dbProps: {},
            updateId: null,
            rules: [
                value => !!value || 'Required',
                value => (value && value.length >= 3) || 'Min 3 characters',
            ],
            connectionState
        }),
        mounted() {
            setTimeout(this.getAllDatabases, 100);
        },
        methods: {
            ...mapMutations(["updateDatabase"]),
            ...mapMutations(["updateDatabases"]),
            ...mapMutations(["addDatabase"]),
            ...mapActions(["fetchDatabases"]),
            getAllDatabases() {
                this.loading = true;
                this.fetchDatabases().then(() => {
                    databaseStatusFetch();
                    this.loading = false;
                });
            },
            saveOrUpdate() {
                if (this.$refs.form.validate()) {
                    if (this.update) {
                        axios.patch(`/database/${this.updateId}`, this.input, {params: this.dbProps})
                            .then(response => {
                                if (response) {
                                    let database = response.data;
                                    this.updateDatabase(database);
                                    this.closeDialog();
                                }
                            })
                    } else {
                        axios.put("/database/" + this.select, this.input, {params: this.dbProps})
                            .then(response => {
                                if (response) {
                                    let database = response.data;
                                    database.icon = dbManager.findDbByType(database.type).icon;
                                    database.configs = JSON.parse(database.configs);
                                    this.addDatabase(database);
                                    this.closeDialog();
                                }
                            })
                    }
                }

            },
            closeDialog() {
                this.$refs.form.reset();
                this.dialog = false;
                this.update = false;
                this.select = null;
                this.fields = [];
                this.input = {};
                this.dbProps = {};
                this.updateId = null;
            },
            openUpdateDialog(database) {
                this.update = true;
                this.updateId = database.id;
                this.select = database.type;
                this.fields = dbManager.findDbByType(database.type);
                this.input = {...database.configs};
                this.dbProps = {
                    workerId: database.workerId,
                    alias: database.alias
                };
                this.dialog = true;
            },
            setDeleteDialog(deleteDialog, deleteCandidate) {
                this.deleteDialog = deleteDialog;
                this.deleteCandidate = deleteCandidate;
            },
            deleteDatabase() {
                axios.delete("/database/" + this.deleteCandidate.id)
                    .then(response => {
                        if (response) {
                            let databases = this.getDatabases.filter(element => element !== this.deleteCandidate);
                            this.updateDatabases(databases);
                            this.setDeleteDialog(false, null);
                        }
                    })
            },
            getWorkerById(item){
                return this.getWorkers.find(worker => worker.id === item);
            },
            findDbByType(type){
                return dbManager.findDbByType(type);
            }

        },
        watch: {
            select(value) {
                if (value) {
                    this.fields = dbManager.findDbByType(value).fields;
                }
            }
        },
        computed: {
            getDatabaseTypes(){
                return dbManager.getDatabaseTypes();
            },
            ...mapGetters(["getWorkers"]),
            ...mapGetters(["getDatabases"]),
            workersIds() {
                return this.getWorkers.map(worker => worker.id);
            }
        }
    }
</script>

<style scoped>

</style>
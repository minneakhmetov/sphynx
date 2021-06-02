<template>
    <v-container>
        <v-row justify="center">
            <v-dialog
                    v-model="deleteDialog"
                    max-width="400"
            >
                <v-card>
                    <v-card-title class="headline">
                        Are you sure to delete {{deleteCandidate ? deleteCandidate.alias : null}}?
                    </v-card-title>

                    <v-card-text>
                        This action cannot be reverted
                    </v-card-text>

                    <v-card-actions>
                        <v-spacer></v-spacer>

                        <v-btn
                                color="red darken-1"
                                text
                                @click="deleteWorkerCandidate"
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
                            + Add new worker
                        </v-btn>
                    </template>
                    <v-card>
                        <v-card-title>
                            <span class="headline">{{this.update ? 'Update ' + this.updateAlias : 'Adding new worker'}}</span>
                        </v-card-title>
                        <v-card-text>
                            <v-form ref="form" v-on:submit.prevent="">
                                <v-container>
                                    <v-row>
                                        <v-col cols="12">
                                            <v-select
                                                    v-model="input.version"
                                                    :items="versions"
                                                    label="Select version"
                                                    text
                                                    required
                                            >
                                            </v-select>
                                        </v-col>
                                    </v-row>
                                    <v-row>
                                        <v-col>
                                            <v-text-field
                                                    label="Alias"
                                                    :rules="rules"
                                                    hide-details="auto"
                                                    v-model="input.alias"
                                                    text
                                            ></v-text-field>
                                        </v-col>
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
                                    @click="resetKeyButton"
                                    v-if="this.update"
                            >
                                Reset key
                            </v-btn>
                            <v-btn
                                    color="blue darken-1"
                                    text
                                    @click="saveButtonDialog"
                                    :disabled="input.saveDisabled"
                            >
                                Save
                            </v-btn>
                        </v-card-actions>
                        <v-card-text v-if="key === 'loading'">
                            <v-container>
                                <v-row>
                                    <v-col class="d-flex align-center justify-center pa-4 mx-auto">
                                        <v-progress-circular
                                                indeterminate
                                                color="amber darken-3"
                                        ></v-progress-circular>
                                    </v-col>
                                </v-row>
                            </v-container>
                        </v-card-text>
                        <v-card-text v-else-if="key">
                            <v-container>
                                <v-row>
                                    <v-col class="pb-0">
                                        <h3 class="subtitle-1">Please save the key and put to worker's configuration
                                            file:</h3>
                                    </v-col>
                                </v-row>
                                <v-row>
                                    <v-col>
                                        <v-alert
                                                text
                                                outlined
                                                color="amber darken-3"
                                                class="mb-0"
                                                style="word-break: break-all"
                                        >
                                            {{key}}
                                        </v-alert>
                                    </v-col>
                                </v-row>
                            </v-container>
                        </v-card-text>
                        <v-card-text v-else>
                        </v-card-text>
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
                    <div v-if="getWorkers.length">
                        <v-list-item class="mb-4 elevation-3"
                                     v-for="(worker, i) in getWorkers"
                                     v-bind:key="i">
                            <v-list-item-content>
                                <v-row align="center" justify="center" style="height: 4.8rem">
                                    <v-col cols="1">
                                        <div align="center">
                                            <v-icon>mdi-server-network</v-icon>
                                        </div>
                                    </v-col>
                                    <v-col cols="5" class="pl-0">
                                        <v-list-item-title style="font-size: 140%">{{worker.alias}}
                                        </v-list-item-title>
                                    </v-col>
                                    <v-col cols="6" class="text-right"
                                           style="display: flex; align-items: center; justify-content: flex-end">
                                        <v-progress-circular
                                                v-if="worker.loading"
                                                indeterminate
                                                color="amber darken-3"
                                                :size="20"
                                                style="margin-right: 3%"
                                        ></v-progress-circular>
                                        <div v-else class="font-weight-medium" style="margin-right: 3%">
                                            <div :style="{color: worker.connectionState === 'CONNECTED' ? '#43a047' : '#e53935'}">
                                                {{worker.connectionState}}
                                            </div>
                                        </div>
                                        <v-btn
                                                elevation="0"
                                                class="sphynx-secondary mr-3"
                                                @click.stop="openUpdateDialog(worker)"
                                        >
                                            Edit
                                        </v-btn>
                                        <v-btn
                                                color="red"
                                                dark
                                                @click.stop="setDeleteDialog(true, worker)"
                                        >
                                            <v-icon>mdi-delete</v-icon>
                                        </v-btn>
                                    </v-col>
                                </v-row>
                            </v-list-item-content>
                        </v-list-item>
                    </div>
                    <div class="d-flex align-center justify-center pa-4 mx-auto" v-else>
                        <div class="text-subtitle-1">
                            There are no one registered workers. <a @click="dialog = true">Create the first</a>
                        </div>
                    </div>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
    import {mapGetters, mapActions} from 'vuex';
    import axios from "../axios";

    export default {
        name: "WorkerView",
        computed: mapGetters(["getWorkers"]),
        mounted() {
            axios.get("/worker/versions")
                .then(response => {
                    if (response) {
                        this.versions = response.data;
                    }
                });
            this.refetchWorkers();
        },
        data: () => ({
            dialog: false,
            input: {},
            key: null,
            update: false,
            deleteDialog: false,
            deleteCandidate: null,
            updateAlias: '',
            versions: [],
            worker: null,
            rules: [
                value => !!value || 'Required',
                value => (value && value.length >= 3) || 'Min 3 characters',
            ],
        }),
        methods: {
            ...mapActions(["fetchVersions"]),
            ...mapActions(["fetchWorkers"]),
            ...mapActions(["fetchWorker"]),
            ...mapActions(["refetchWorkers"]),
            ...mapActions(["addWorker"]),
            ...mapActions(["updateWorker"]),
            ...mapActions(["resetKey"]),
            ...mapActions(["deleteWorker"]),
            closeDialog() {
                this.$refs.form.reset();
                this.dialog = false;
                this.update = false;
                this.select = null;
                this.input = {};
                this.key = null;
                this.worker = null;
            },
            openUpdateDialog(worker) {
                this.update = true;
                this.updateAlias = worker.alias;
                this.input = JSON.parse(JSON.stringify(worker));
                this.worker = worker;
                this.dialog = true;
            },
            setDeleteDialog(deleteDialog, deleteCandidate) {
                this.deleteDialog = deleteDialog;
                this.deleteCandidate = deleteCandidate;
            },
            saveButtonDialog() {
                if (this.$refs.form.validate()) {
                    if (!this.update) {
                        this.key = 'loading';
                        this.input.saveDisabled = true;
                        const body = {
                            alias: this.input.alias,
                            version: this.input.version
                        };
                        this.addWorker(body).then(response => {
                            setTimeout(() => this.key = response.key, 500);
                        })
                    } else {
                        this.updateWorker(this.input);
                        this.closeDialog();
                    }
                }
            },
            resetKeyButton() {
                this.key = 'loading';
                this.resetKey(this.input.id).then(response => {
                    setTimeout(() => {
                            this.key = response.key;
                            this.worker.connectionState = 'DISCONNECTED';
                        }, 500
                    );
                })
            },
            deleteWorkerCandidate() {
                this.deleteWorker(this.deleteCandidate.id);
                this.setDeleteDialog(false, null);
            }
        }
    }
</script>

<style scoped>

</style>
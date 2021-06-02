<template>
    <v-container>
        <v-row>
            <v-col>
                <v-btn
                        elevation="0"
                        class="sphynx-secondary mt-4"
                        @click="startTest"
                        v-if="buttonIcon.includes('START')"
                >
                    <v-icon class="mr-2">mdi-play</v-icon>
                    Start
                </v-btn>
                <v-btn
                        elevation="0"
                        class="sphynx-secondary mt-4 mr-2"
                        @click="resumeExecution"
                        v-if="buttonIcon.includes('RESUME')"
                >
                    <v-icon class="mr-2">mdi-play-pause</v-icon>
                    Resume
                </v-btn>
                <v-btn
                        elevation="0"
                        class="sphynx-secondary mt-4 mr-2"
                        @click="pauseExecution"
                        v-if="buttonIcon.includes('PAUSE')"
                >
                    <v-icon class="mr-2">mdi-pause</v-icon>
                    Pause
                </v-btn>
                <v-btn
                        elevation="0"
                        class="sphynx-secondary mt-4"
                        @click="terminateExecution"
                        v-if="buttonIcon.includes('TERMINATE')"
                >
                    <v-icon class="mr-2">mdi-stop</v-icon>
                    Terminate
                </v-btn>
            </v-col>

        </v-row>
        <v-row>
            <v-col>
                <v-card
                        class="mx-auto"
                        tile
                        elevation="0"

                >
                    <div v-if="executions.length">
                        <v-list-item class="mb-4 elevation-3"
                                     v-for="(execution, i) in executions"
                                     :key="i"
                        >
                            <v-list-item-content>
                                <v-progress-linear
                                        :active="testState[execution.state].buttonIcon.includes('TERMINATE')"
                                        v-if="testState[execution.state].buttonIcon.includes('TERMINATE')"
                                        :indeterminate="testState[execution.state].prepare"
                                        :query="testState[execution.state].buttonIcon.includes('TERMINATE')"
                                        :value="execution.process"
                                        absolute
                                        top
                                        color="amber darken-3"
                                ></v-progress-linear>
                                <v-row align="center" justify="center" style="height: 4.8rem">
                                    <v-col cols="1">
                                        <div align="center">
                                            <h2>
                                                {{`#${executions.length - i}.`}}
                                            </h2>
                                        </div>
                                    </v-col>
                                    <v-col cols="3"
                                           style="display: flex; align-items: center; justify-content: flex-end">
                                        <v-icon v-if="testState[execution.state].icon" class="mr-2"
                                                :style="testState[execution.state].style">
                                            {{testState[execution.state].icon}}
                                        </v-icon>
                                        <v-progress-circular
                                                v-else
                                                indeterminate
                                                :color="testState[execution.state].style.color"
                                                size="20"
                                                class="mr-2"
                                        ></v-progress-circular>
                                        <v-list-item-title :style="testState[execution.state].style">{{execution.state}}
                                        </v-list-item-title>
                                    </v-col>
                                    <v-col cols="2" class="pl-0">
                                        <v-list-item-title style="font-weight: bold">Started time:
                                        </v-list-item-title>
                                        <v-list-item-title>{{getTime(execution.startTime)}}
                                        </v-list-item-title>
                                    </v-col>
                                    <v-col cols="2" class="pl-0" v-if="execution.endTime">
                                        <v-list-item-title style="font-weight: bold">End time:
                                        </v-list-item-title>
                                        <v-list-item-title>{{getTime(execution.endTime)}}
                                        </v-list-item-title>
                                    </v-col>
                                    <v-col v-else></v-col>

                                    <v-col cols="4" style="display: flex; align-items: center; justify-content: flex-end">
                                        <v-btn
                                                elevation="0"
                                                class="sphynx-secondary mr-2"
                                                @click.stop="redirectToMetrics(execution)"
                                        >
                                            Metrics
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
                            There are no one execution now.
                            <!--                            <a @click="dialog = true">Create the first</a>-->
                        </div>
                    </div>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
    import axios from "../axios";
    import testState from "../constants/teststate";
    import stompClient from "../socket";
    import {mapGetters} from "vuex";
    import router from '../router';

    export default {
        name: "ExecutionView",
        props: {
            id: String
        },
        data: () => ({
            loading: false,
            executions: [],
            subscription: null,
            processSubscription: null,
            buttonIcon: ['START'],
            running: null,
            testState
        }),
        mounted() {
            this.getExecutions();
        },
        destroyed() {
            if (this.subscription && this.processSubscription) {
                this.subscription.then(sub => sub.unsubscribe());
                this.processSubscription.then(sub => sub.unsubscribe());
            }
        },
        methods: {
            getExecutions() {
                this.loading = true;
                let testId = this.$route.params.id;
                axios.get("/execution/test/" + testId).then(response => {
                    if (response.data) {
                        this.executions = response.data.reverse();
                        let execution = this.executions.find(e => {
                            let buttonIcon = testState[e.state].buttonIcon;
                            let includes = buttonIcon.includes('TERMINATE');
                            if (includes) {
                                this.buttonIcon = buttonIcon;
                                this.running = e.id;
                            }
                            return includes;
                        });
                        if (execution) {
                            this.subscribe(testId, execution);
                        }
                        this.loading = false;
                    }
                })
            },
            startTest() {
                let testId = this.$route.params.id;
                axios.post("/execution/start/test/" + testId).then(response => {
                    if (response) {
                        this.executions.unshift(response.data);
                        let execution = this.executions.find(execution => execution.id === response.data.id);
                        this.buttonIcon = testState[execution.state].buttonIcon;
                        this.running = response.data.id;
                        this.subscribe(testId, execution);
                    }
                })
            },
            getTime(array) {
                return `${this.getZero(array[2])}.${this.getZero(array[1])}.${array[0]} ${this.getZero(array[3])}:${this.getZero(array[4])}:${this.getZero(array[5])}`
            },
            getZero(number) {
                return number > 9 ? number : `0${number}`
            },
            subscribe(testId, execution) {
                this.subscription = stompClient.subscribe(`/test/${testId}/execution/${execution.id}`, (updated) => {
                    let body = JSON.parse(updated.body);
                    execution.testConfigsId = body.testConfigsId;
                    execution.state = body.state;
                    execution.startTime = body.startTime;
                    execution.endTime = body.endTime;
                    execution.userId = body.userId;
                    execution.process = body.process;
                    this.buttonIcon = testState[execution.state].buttonIcon;
                    if (this.buttonIcon.includes('START')) {
                        this.subscription.then(sub => sub.unsubscribe());
                        this.processSubscription.then(sub => sub.unsubscribe());
                        this.subscription = null;
                        this.running = null;
                        this.processSubscription = null;
                    }
                }, {'id-sub' : 0});
                this.processSubscription = stompClient.subscribe(`/test/${testId}/execution/${execution.id}/process`, (updated) => {
                    let body = JSON.parse(updated.body);
                    execution.process = body.process;
                }, {'id-sub' : 1});
            },
            pauseExecution() {
                axios.post("/execution/pause/" + this.running).then(response => {
                    if (response) {
                        let execution = this.executions.find(execution => execution.id === this.running);
                        execution.state = 'PENDING';
                        this.buttonIcon = testState['PAUSED'].buttonIcon;
                    }
                });
            },
            resumeExecution() {
                axios.post("/execution/resume/" + this.running).then(response => {
                    if (response) {
                        let execution = this.executions.find(execution => execution.id === this.running);
                        execution.state = 'PENDING';
                        this.buttonIcon = testState['RUNNING'].buttonIcon;
                    }
                });
            },
            terminateExecution() {
                axios.post("/execution/terminate/" + this.running).then(response => {
                    if (response) {
                        let execution = this.executions.find(execution => execution.id === this.running);
                        execution.state = 'PENDING';
                        this.buttonIcon = testState['TERMINATED'].buttonIcon;
                        this.running = null;
                    }
                });
            },
            redirectToMetrics(execution){
                router.push(`/tests/${execution.testConfigsId}/executions/${execution.id}`)
            }
        },
        computed: {
            ...mapGetters(["getAuth"]),
        }
    }
</script>

<style scoped>

</style>
<template>
    <v-container>
        <v-row justify="center">
            <v-dialog
                    v-model="dialogWebhook"
                    persistent
                    max-width="600px"
            >
                <v-card>
                    <v-card-title>
                        <span class="headline">{{webhooksUpdate ? 'Update ' + webhooksModelCandidate.alias : 'Add new webhook'}}</span>
                    </v-card-title>
                    <v-card-text>
                        <v-container>
                            <v-row>
                                <v-col class="pb-0">
                                    <v-select
                                            v-model="webhooksModelCandidate.webhookType"
                                            :items="webhooksConfigs.map(config => config.type)"
                                            label="Select type"
                                            required
                                            :rules="[(v) => !!v || 'Required']"
                                    ></v-select>
                                </v-col>
                            </v-row>
                            <v-row>
                                <v-col>
                                    <v-text-field
                                            label="Alias"
                                            :rules="rules"
                                            v-model="webhooksModelCandidate.alias"
                                            required
                                    ></v-text-field>
                                </v-col>
                            </v-row>
                        </v-container>
                        <v-container
                                v-if="webhooksConfigs.find(configs => configs.type === webhooksModelCandidate.webhookType)">
                            <v-row v-for="(fieldName, i) in Object.keys(webhooksConfigs.find(configs => configs.type === webhooksModelCandidate.webhookType).fields)"
                                   :key="i">
                                <v-col>
                                    <v-text-field
                                            :label="webhooksConfigs.find(configs => configs.type === webhooksModelCandidate.webhookType).fields[fieldName].name"
                                            :rules="webhooksConfigs.find(configs => configs.type === webhooksModelCandidate.webhookType).fields[fieldName].rules"
                                            v-model="webhooksModelCandidate.configs[fieldName]"
                                            required
                                    ></v-text-field>
                                </v-col>
                            </v-row>
                        </v-container>
                    </v-card-text>
                    <v-card-actions>
                        <v-spacer></v-spacer>
                        <v-btn
                                color="blue darken-1"
                                text
                                @click="closeWebhookDialog()"
                        >
                            Close
                        </v-btn>
                        <v-btn
                                color="blue darken-1"
                                text
                                @click="saveWebhookDialog()"
                        >
                            {{webhooksUpdate ? 'Update' : 'Save'}}
                        </v-btn>
                    </v-card-actions>
                </v-card>
            </v-dialog>
        </v-row>
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
                        This action cannot be reverted
                    </v-card-text>

                    <v-card-actions>
                        <v-spacer></v-spacer>

                        <v-btn
                                color="red darken-1"
                                text
                                @click="deleteTest"
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
            <v-col cols="auto">
                <v-dialog max-width="800" v-model="dialog">
                    <template>
                        <v-stepper v-model="stepNumber">
                            <v-stepper-header>
                                <v-stepper-step
                                        :complete="stepNumber > 1"
                                        step="1"
                                >
                                    Select database
                                </v-stepper-step>

                                <v-divider></v-divider>

                                <v-stepper-step
                                        :complete="stepNumber > 2"
                                        step="2"
                                >
                                    Configure Test
                                </v-stepper-step>

                                <v-divider></v-divider>

                                <v-stepper-step step="3" :complete="stepNumber > 3">
                                    Test mode
                                </v-stepper-step>

                                <v-divider></v-divider>

                                <v-stepper-step step="4">
                                    Schedule & Webhooks
                                </v-stepper-step>
                            </v-stepper-header>

                            <v-stepper-items>
                                <v-stepper-content step="1">
                                    <v-form ref="databases">
                                        <v-select
                                                v-model="select.databaseId"
                                                :items="getDatabasesIds"
                                                label="Select database"
                                                required
                                                class="mb-6"
                                                :rules="[(v) => !!v || 'Required']"
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
                                                <div class="font-weight-medium" align="right"
                                                     :style="{color: connectionState[getDatabaseById(item).connectionState].color, textAlign: 'right',}">
                                                    {{getDatabaseById(item).connectionState}}
                                                </div>
                                            </template>
                                        </v-select>
                                        <v-row>
                                            <v-col style="display: flex; align-items: center; justify-content: flex-end">
                                                <v-btn
                                                        text
                                                        @click="closeDialog">
                                                    Cancel
                                                </v-btn>
                                                <v-btn
                                                        color="amber darken-3"
                                                        @click="configureTest"
                                                >
                                                    Continue
                                                </v-btn>
                                            </v-col>
                                        </v-row>
                                    </v-form>
                                </v-stepper-content>

                                <v-stepper-content step="2">
                                    <v-form ref="testConfigs">
                                        <v-container fluid>
                                            <v-row>
                                                <v-col>
                                                    <v-text-field
                                                            label="Test Name"
                                                            :rules="rules"
                                                            hide-details="auto"
                                                            v-model="select.name"
                                                            text
                                                            class="pt-0"
                                                    ></v-text-field>
                                                </v-col>
                                            </v-row>
                                            <v-row v-for="(fieldName, i) in Object.keys(getTestConfigs)" :key="i">
                                                <v-col class="pb-0 pt-0">
                                                    <v-text-field
                                                            v-if="getTestConfigs[fieldName].inputType === 'text'"
                                                            :label="getTestConfigs[fieldName].name"
                                                            :rules="getTestConfigs[fieldName].rules"
                                                            v-model="testConfigs[fieldName]"
                                                            required
                                                    ></v-text-field>
                                                    <v-switch
                                                            v-if="getTestConfigs[fieldName].inputType === 'switch'"
                                                            v-model="testConfigs[fieldName]"
                                                            :label="getTestConfigs[fieldName].name"
                                                            required
                                                    ></v-switch>
                                                </v-col>
                                            </v-row>
                                            <v-row>
                                                <v-col>
                                                    <v-btn
                                                            color="amber darken-3"
                                                            @click="stepNumber = 1"
                                                    >
                                                        Previous
                                                    </v-btn>
                                                </v-col>
                                                <v-col style="display: flex; align-items: center; justify-content: flex-end">
                                                    <v-btn
                                                            text
                                                            @click="closeDialog">
                                                        Cancel
                                                    </v-btn>

                                                    <v-btn
                                                            color="amber darken-3"
                                                            @click="configureTestConfigs"
                                                    >
                                                        Continue
                                                    </v-btn>
                                                </v-col>
                                            </v-row>
                                        </v-container>
                                    </v-form>
                                </v-stepper-content>

                                <v-stepper-content step="3">
                                    <v-form ref="testMode">
                                        <v-container fluid>
                                            <v-row>
                                                <v-col>
                                                    <v-select
                                                            :items="getTestModes"
                                                            v-model="select.testMode"
                                                            label="Test mode"
                                                            :rules="[(v) => !!v || 'Required']"
                                                    ></v-select>
                                                </v-col>
                                            </v-row>
                                            <v-row v-for="(fieldName, i) in Object.keys(getTestModeFields)" :key="i">
                                                <v-col>
                                                    <v-text-field
                                                            v-if="getTestModeFields[fieldName].inputType === 'text'"
                                                            :label="getTestModeFields[fieldName].name"
                                                            :rules="getTestModeFields[fieldName].rules"
                                                            v-model="testModeConfig[fieldName]"
                                                            required
                                                    ></v-text-field>
                                                    <v-switch
                                                            v-if="getTestModeFields[fieldName].inputType === 'switch'"
                                                            v-model="testModeConfig[fieldName]"
                                                            :label="getTestModeFields[fieldName].name"
                                                            required
                                                    ></v-switch>
                                                </v-col>
                                            </v-row>
                                            <v-row>
                                                <v-col>
                                                    <v-btn
                                                            color="amber darken-3"
                                                            @click="stepNumber = 2"
                                                    >
                                                        Previous
                                                    </v-btn>
                                                </v-col>
                                                <v-col style="display: flex; align-items: center; justify-content: flex-end">
                                                    <v-btn
                                                            text
                                                            @click="closeDialog">
                                                        Cancel
                                                    </v-btn>

                                                    <v-btn
                                                            color="amber darken-3"
                                                            @click="configureSchedule"
                                                    >
                                                        Continue
                                                    </v-btn>
                                                </v-col>
                                            </v-row>

                                        </v-container>
                                    </v-form>
                                </v-stepper-content>

                                <v-stepper-content step="4">
                                    <v-form ref="schedule">
                                        <v-container>
                                            <v-row>
                                                <v-col>
                                                    <v-combobox
                                                            label="Repeat periodically (cron expression)"
                                                            :rules="cronRules"
                                                            hide-details="auto"
                                                            v-model="select.cron"
                                                            :items="repeatMacroses"
                                                    ></v-combobox>
                                                </v-col>
                                            </v-row>
                                            <v-row>
                                                <v-col>
                                                    <v-alert
                                                            text
                                                            dense
                                                            color="teal"
                                                            icon="mdi-clock-fast"
                                                            border="left"
                                                            v-if="this.select.cron"
                                                    >
                                                        Next trigger date {{getNextDate}}
                                                    </v-alert>
                                                </v-col>
                                            </v-row>
                                            <v-row>
                                                <v-col class="pb-0">
                                                    <h4>Webhooks:</h4>
                                                </v-col>
                                            </v-row>
                                            <v-row>
                                                <v-col class="pr-0 pl-0 pt-0">
                                                    <v-list
                                                            nav
                                                            dense
                                                            tile
                                                            v-if="showWebhooks"
                                                    >
                                                        <v-list-item-group
                                                                class="pt-0"
                                                        >
                                                            <v-list-item
                                                                    v-for="(item, i) in getFilteredWebhooks"
                                                                    :key="i"
                                                                    class="sphynx-secondary"
                                                                    v-model="selectedWebhook"
                                                                    @click="selectWebhook(i)"
                                                            >
                                                                <v-container>
                                                                    <v-row align="center">
                                                                        <v-col cols="1" align="center" justify="center">
                                                                            <v-icon v-if="webhooksConfigs.find(config => config.type === item.webhookType).icon"
                                                                                    v-text="webhooksConfigs.find(config => config.type === item.webhookType).icon"></v-icon>
                                                                            <img align="center"
                                                                                 v-if="webhooksConfigs.find(config => config.type === item.webhookType).picture"
                                                                                 :src="webhooksConfigs.find(config => config.type === item.webhookType).picture"
                                                                                 height="35rem">
                                                                        </v-col>
                                                                        <v-col cols="2">
                                                                            <h4>{{webhooksConfigs.find(config =>
                                                                                config.type ===
                                                                                item.webhookType).text}}</h4>
                                                                        </v-col>
                                                                        <v-col cols="7">
                                                                            <h4>{{item.alias}}</h4>
                                                                        </v-col>
                                                                        <v-col cols="2" align="right">
                                                                            <v-btn
                                                                                    color="red"
                                                                                    dark
                                                                                    @click.stop="webhooksDeleted.push(i)"
                                                                            >
                                                                                <v-icon>mdi-delete</v-icon>
                                                                            </v-btn>
                                                                        </v-col>
                                                                    </v-row>
                                                                </v-container>
                                                            </v-list-item>
                                                        </v-list-item-group>
                                                        <v-container>
                                                            <v-row>
                                                                <v-col class="pr-0 pl-0">
                                                                    <v-btn
                                                                            elevation="0"
                                                                            class="sphynx-secondary"
                                                                            block
                                                                            @click="createWebhook"
                                                                    >
                                                                        + Add new webhook
                                                                    </v-btn>
                                                                </v-col>
                                                            </v-row>
                                                        </v-container>
                                                    </v-list>
                                                    <div class="d-flex align-center justify-center pa-4 mx-auto" v-else>
                                                        <div class="text-subtitle-1">
                                                            There are no one webhook now. <a @click="createWebhook">Create the first</a>
                                                        </div>
                                                    </div>
                                                </v-col>
                                            </v-row>
                                            <v-row>
                                                <v-col>
                                                    <v-btn
                                                            color="amber darken-3"
                                                            @click="stepNumber = 3"
                                                    >
                                                        Previous
                                                    </v-btn>
                                                </v-col>
                                                <v-col style="display: flex; align-items: center; justify-content: flex-end">
                                                    <v-btn
                                                            text
                                                            @click="closeDialog">
                                                        Cancel
                                                    </v-btn>

                                                    <v-btn
                                                            color="amber darken-3"
                                                            @click="createTest"
                                                    >
                                                        {{this.update ? 'Update' : 'Create'}}
                                                    </v-btn>
                                                </v-col>
                                            </v-row>
                                        </v-container>
                                    </v-form>
                                </v-stepper-content>

                            </v-stepper-items>
                        </v-stepper>
                    </template>
                </v-dialog>
            </v-col>
        </v-row>
        <v-row>
            <v-col>
                <v-btn
                        elevation="0"
                        class="sphynx-secondary"
                        @click="openDialog"
                >
                    + Add new test
                </v-btn>
            </v-col>
        </v-row>
        <v-row justify="center" v-if="loading">
            <v-progress-circular
                    indeterminate
                    color="amber darken-3"
            ></v-progress-circular>
        </v-row>
        <v-row justify="center" v-else-if="tests.length">
            <v-col>
                <v-expansion-panels accordion focusable v-model="panel">
                    <v-expansion-panel
                            v-for="(item, i) in tests"
                            :key="i" class="mb-4 elevation-2"
                    >
                        <v-expansion-panel-header>
                            <v-progress-linear
                                    :active="testState[item.state].buttonIcon.includes('TERMINATE')"
                                    v-if="testState[item.state].buttonIcon.includes('TERMINATE')"
                                    :indeterminate="testState[item.state].prepare"
                                    :query="testState[item.state].buttonIcon.includes('TERMINATE')"
                                    :value="item.process"
                                    absolute
                                    top
                                    color="amber darken-3"
                            ></v-progress-linear>
                            <v-row align="center" justify="center" style="height: 4.8rem">
                                <v-col cols="1" class="pl-0 pr-0">
                                    <div align="center">
                                        <img
                                                :src="findDbByType(item.databaseType).icon" style="max-width: 3rem"
                                        />
                                    </div>
                                </v-col>
                                <v-col cols="2" class="pl-0">
                                    <v-list-item-title style="font-size: 140%">{{item.name}}
                                    </v-list-item-title>
                                </v-col>
                                <v-col cols="3">
                                    <v-icon style="max-width: 1.7rem; margin-right: 3%">
                                        mdi-server-network
                                    </v-icon>
                                    {{ getWorkerAlias(item)}}
                                </v-col>
                                <v-col cols="6" class="text-right"
                                       style="display: flex; align-items: center; justify-content: flex-end">
                                    <v-icon v-if="testState[item.state].icon" class="mr-2"
                                            :style="testState[item.state].style">
                                        {{testState[item.state].icon}}
                                    </v-icon>
                                    <v-progress-circular
                                            v-else
                                            indeterminate
                                            :color="testState[item.state].style.color"
                                            size="20"
                                            class="mr-2"
                                    ></v-progress-circular>
                                    <p class="mr-3 mb-0" :style="testState[item.state].style">{{item.state}}
                                    </p>
                                    <v-btn
                                            elevation="0"
                                            class="sphynx-secondary mr-2"
                                            v-if="testState[item.state].buttonIcon.includes('START')"
                                            @click.stop="startTest(item)"
                                    >
                                        <v-icon>mdi-play</v-icon>
                                    </v-btn>
                                    <v-btn
                                            elevation="0"
                                            class="sphynx-secondary mr-2"
                                            @click.stop="setTestState(item, '/execution/resume/')"
                                            v-if="testState[item.state].buttonIcon.includes('RESUME')"
                                    >
                                        <v-icon>mdi-play-pause</v-icon>
                                    </v-btn>
                                    <v-btn
                                            elevation="0"
                                            class="sphynx-secondary mr-2"
                                            @click.stop="setTestState(item, '/execution/pause/')"
                                            v-if="testState[item.state].buttonIcon.includes('PAUSE')"
                                    >
                                        <v-icon>mdi-pause</v-icon>
                                    </v-btn>
                                    <v-btn
                                            elevation="0"
                                            class="sphynx-secondary mr-2"
                                            @click.stop="setTestState(item, '/execution/terminate/')"
                                            v-if="testState[item.state].buttonIcon.includes('TERMINATE')"
                                    >
                                        <v-icon>mdi-stop</v-icon>
                                    </v-btn>
                                    <v-btn
                                            elevation="0"
                                            class="sphynx-secondary mr-2"
                                            @click.stop="openExecutions(item)"
                                    >
                                        Executions
                                    </v-btn>
                                    <v-btn
                                            elevation="0"
                                            class="sphynx-secondary mr-2"
                                            @click.stop="editTest(item)"
                                    >
                                        Edit
                                    </v-btn>
                                    <v-btn
                                            color="red"
                                            dark
                                            class="mr-3"
                                            @click.stop="setDeleteDialog(true, item)"
                                    >
                                        <v-icon>mdi-delete</v-icon>
                                    </v-btn>
                                </v-col>
                            </v-row>
                        </v-expansion-panel-header>
                        <v-expansion-panel-content eager>
                            <div class="d-flex align-center justify-center pa-4 mx-auto" v-if="iterationsLoading">
                                <v-progress-circular
                                        indeterminate
                                        color="amber darken-3"
                                ></v-progress-circular>
                            </div>
                            <div v-else-if="iterations.length">
                                <v-card
                                        class="mx-auto"
                                        tile
                                >
                                    <v-list-item v-for="(iteration, i) in iterations" :key="i"
                                                 :style="{backgroundColor : iteration.edit === 'BLOCK' ? '#ec407a' : iteration.edit === 'CREATE' ? '#dcedc8' : iteration.clean ? '#e3f2fd' : 'white' }">
                                        <v-container>
                                            <v-form :ref="`iteration_${i}`">
                                                <v-row justify="center" align="center">
                                                    <v-col cols="6">
                                                        <h3 v-if="iteration.edit === 'SAVED'"
                                                            class="font-weight-medium">
                                                            {{`#${i + 1}. ${iteration.name}`}}</h3>
                                                        <div v-else
                                                             style="display: flex; align-items: center; justify-content: flex-start">
                                                            <h2 class="font-weight-medium mr-4">
                                                                {{`#${i + 1}.`}}</h2>
                                                            <v-text-field
                                                                    label="Iteration name"
                                                                    :rules="rules"
                                                                    v-model="iteration.name"
                                                                    hide-details="auto"
                                                            ></v-text-field>
                                                        </div>
                                                    </v-col>
                                                    <v-col cols="6" class="text-right">
                                                        <v-btn
                                                                color="blue darken-1"
                                                                text
                                                                v-if="iteration.edit === 'EDIT'"
                                                                @click="deleteIteration(iteration, i, item.id)"
                                                        >
                                                            Delete
                                                        </v-btn>
                                                        <v-btn
                                                                color="blue darken-1"
                                                                text
                                                                v-if="iteration.edit === 'SAVED'"
                                                                @click="handleEdit(iteration, i)"
                                                        >
                                                            Edit
                                                        </v-btn>
                                                        <v-btn
                                                                color="blue darken-1"
                                                                text
                                                                v-if="iteration.edit !== 'SAVED'"
                                                                @click="handleSave(iteration, i)"
                                                        >
                                                            Save
                                                        </v-btn>
                                                        <v-btn
                                                                color="blue darken-1"
                                                                text
                                                                @click="handleCancel(iteration, i)"
                                                                v-if="iteration.edit !== 'SAVED'"
                                                        >
                                                            Cancel
                                                        </v-btn>
                                                    </v-col>
                                                </v-row>
                                                <v-row>
                                                    <v-col class="pt-0 pb-0">
                                                        <v-textarea
                                                                label="SQL query"
                                                                auto-grow
                                                                outlined
                                                                rows="4"
                                                                :rules="rules"
                                                                v-model="iteration.sql"
                                                                :disabled="iteration.edit === 'SAVED' "
                                                        >{{iteration.sqlHtml}}
                                                        </v-textarea>
                                                        <!--                                                        <span v-html="iteration.sqlHtml"></span>-->
                                                    </v-col>
                                                </v-row>
                                                <v-row>
                                                    <v-col cols="3">
                                                        <v-switch
                                                                v-model="iteration.clean"
                                                                label="Clean"
                                                                :disabled="iteration.edit === 'SAVED'"
                                                        ></v-switch>
                                                    </v-col>
                                                    <v-col cols="3">
                                                        <v-select
                                                                v-model="iteration.savedQueryId"
                                                                :items="getSavedQueriesIds"
                                                                label="Compare equality to saved query"
                                                                class="mt-0"
                                                                :disabled="iteration.edit === 'SAVED'"
                                                        >
                                                            <template slot="no-data">
                                                                <div class="d-flex align-center justify-center pa-4 mx-auto">
                                                                    <v-progress-circular
                                                                            v-if="queriesLoading"
                                                                            indeterminate
                                                                            color="amber darken-3"
                                                                    ></v-progress-circular>
                                                                    <div class="text-subtitle-1" v-else>
                                                                        There are no created saved queries
                                                                    </div>
                                                                </div>
                                                            </template>
                                                            <template v-slot:selection="{ item }">
                                                                <div class="text-subtitle-1">
                                                                    {{ savedQueries.find(query => query.id === item).name }}
                                                                </div>
                                                            </template>
                                                            <template v-slot:item="{ item }">
                                                                <div class="text-subtitle-1">
                                                                    {{  savedQueries.find(query => query.id === item).name }}
                                                                </div>
                                                            </template>
                                                        </v-select>
                                                    </v-col>
                                                    <v-col v-for="(configsName, i) in Object.keys(findDbByType(iteration.databaseType).iterationConfigs)"
                                                           :key="i" cols="3">
                                                        <v-switch
                                                                v-model="iteration.configs[configsName]"
                                                                v-if="findDbByType(iteration.databaseType).iterationConfigs[configsName].inputType === 'switch'"
                                                                :label="findDbByType(iteration.databaseType).iterationConfigs[configsName].name"
                                                                :disabled="iteration.edit === 'SAVED'"
                                                        ></v-switch>
                                                        <v-text-field
                                                                v-else-if="findDbByType(iteration.databaseType).iterationConfigs[configsName].isNumber"
                                                                :label="findDbByType(iteration.databaseType).iterationConfigs[configsName].name"
                                                                :rules="findDbByType(iteration.databaseType).iterationConfigs[configsName].rules"
                                                                hide-details="auto"
                                                                :disabled="iteration.edit === 'SAVED'"
                                                                v-model.number="iteration.configs[configsName]"
                                                        ></v-text-field>
                                                        <v-text-field
                                                                v-else
                                                                :label="findDbByType(iteration.databaseType).iterationConfigs[configsName].name"
                                                                :rules="findDbByType(iteration.databaseType).iterationConfigs[configsName].rules"
                                                                hide-details="auto"
                                                                :disabled="iteration.edit === 'SAVED'"
                                                                v-model="iteration.configs[configsName]"
                                                        ></v-text-field>
                                                    </v-col>
                                                </v-row>
                                            </v-form>
                                        </v-container>

                                        <v-list-item-content>
                                            <v-list-item-title>{{iteration.name}}</v-list-item-title>
                                        </v-list-item-content>
                                    </v-list-item>
                                    <v-container>
                                        <v-row>
                                            <v-col justify="center" align="center">
                                                <v-btn
                                                        color="blue darken-1"
                                                        text
                                                        @click="createIteration(item)"
                                                >
                                                    + Add new iteration
                                                </v-btn>
                                            </v-col>
                                        </v-row>
                                    </v-container>
                                </v-card>
                            </div>
                            <div v-else class="d-flex align-center justify-center pa-4 mx-auto">
                                <div class="text-subtitle-1">
                                    There are no one iteration now.
                                    <a @click="createIteration(item)">Create the first</a>
                                </div>
                            </div>
                        </v-expansion-panel-content>
                    </v-expansion-panel>
                </v-expansion-panels>
            </v-col>
        </v-row>
        <v-row justify="center" v-else>
            <!--            <div class="d-flex align-center justify-center pa-4 mx-auto" >-->
            <div class="text-subtitle-1">
                There are no one test now. <a @click="dialog = true">Create the first</a>
            </div>
            <!--            </div>-->
        </v-row>
    </v-container>
</template>

<script>
    import '../css/styles.sass';
    import '../css/expansion-panel.sass';
    import axios from '../axios';
    import dbManager from "../constants/database";
    import testModeManager from "../constants/testmode";
    import {mapActions, mapGetters} from "vuex";
    import sqlhighlight from "../sql";
    import router from "../router";
    import testState from "../constants/teststate";
    import webhooksConfigs from "../constants/webhooksConfigs";
    import stompClient from "../socket";
    import databaseStatusFetch from "../constants/databaseStatusFetch";
    import cronParser from 'cron-parser';
    import connectionState from "../constants/connectionstate";

    export default {
        name: "TestView",
        data: () => ({
            tests: [],
            testConfigs: {},
            testModeConfig: {},
            select: {},
            panel: null,
            stepNumber: 1,
            iterationsLoading: false,
            databasesLoading: false,
            iterations: [],
            iterationsBackup: [],
            dialog: false,
            update: false,
            loading: false,
            deleteCandidate: null,
            deleteDialog: false,
            dialogWebhook: false,
            queriesLoading: false,
            savedQueries: [],
            connectionState,
            rules: [
                value => !!value || 'Required.',
                value => (value && value.length >= 3) || 'Min 3 characters',
            ],
            subId: 0,
            nextDate: null,
            selectedWebhook: null,
            cronRules: [
                //  value => new RegExp("((\\*|\\?|\\d+((\\/|\\-){0,1}(\\d+))*)\\s*){6}$").test(value) || 'Invalid cron expression, should be 6 characters',
                value => {
                    if (value) {
                        try {
                            const interval = cronParser.parseExpression(value);
                            interval.next().toString();
                            return true;
                        } catch (e) {
                            return e.toString();
                        }
                    } else {
                        return true;
                    }
                }
            ],
            repeatMacroses: [
                "@yearly",
                "@annually",
                "@monthly",
                "@weekly",
                "@daily",
                "@midnight",
                "@hourly",
            ],
            webhooksModelCandidate: {},
            webhooksModelIndex: null,
            webhooksUpdate: false,
            webhooksDeleted: [],
            webhooksConfigs,
            testState
        }),
        mounted() {
            this.getAllTests();
            this.getSavedQueries();
        },
        destroyed() {
            this.tests.forEach(test => {
                if (test.subscription && test.processSubscription) {
                    test.subscription.then(sub => sub.unsubscribe());
                    test.processSubscription.then(sub => sub.unsubscribe());
                }
            })
        },
        methods: {
            ...mapActions(["fetchDatabases"]),
            getAllTests() {
                this.loading = true;
                axios.get("/test").then(response => {
                    if (response.data) {
                        this.tests = response.data.reverse();
                        this.tests.forEach(test => {
                            if (testState[test.state].buttonIcon.includes('TERMINATE')) {
                                this.subscribe(test);
                            }
                            if (test.webhookModels) {
                                test.webhookModels.forEach(model => model.configs = JSON.parse(model.configs));
                            } else {
                                test.webhookModels = [];
                            }
                        });
                        this.loading = false;
                    }
                })
            },
            getIterationsByTest(testId) {
                this.iterationsLoading = true;
                let databaseType = this.tests.find(element => element.id === testId).databaseType;
                axios.get(`/test/${testId}/iterations`).then(response => {
                    setTimeout(() => {
                        this.iterations = response.data;
                        this.iterations.forEach(iteration => {
                            iteration.configs = JSON.parse(iteration.configs);
                            this.$set(iteration, 'edit', 'SAVED');
                            this.$set(iteration, 'databaseType', databaseType);
                            let html = sqlhighlight(iteration.sql);
                            this.$set(iteration, 'sqlHtml', html);
                        });
                        this.iterationsLoading = false;
                        this.iterationsBackup = [];
                    }, 1000);
                })
            },
            findDbByType(type) {
                return dbManager.findDbByType(type);
            },
            getWorkerById(item) {
                return this.getWorkers.find(worker => worker.id === item);
            },
            handleSave(iteration, index) {
                if (iteration.edit === 'CREATE') {
                    let previous = this.iterations.filter((item, i) => i < index && item.edit === 'CREATE');
                    if (previous.length !== 0) {
                        previous.forEach(item => item.edit = 'BLOCK');
                        setTimeout(() => previous.forEach(item => item.edit = 'CREATE'), 500);
                    } else {
                        if (this.$refs[`iteration_${index}`][0].validate()) {
                            let body = {
                                name: iteration.name,
                                clean: iteration.clean,
                                sql: iteration.sql,
                                configs: JSON.stringify(iteration.configs),
                                savedQueryId: iteration.savedQueryId
                            };
                            axios.post(`/test/${iteration.testConfigsId}/iteration`, body)
                                .then(response => {
                                    if (response) {
                                        iteration.id = response.data.id;
                                        iteration.edit = 'SAVED';
                                    }
                                });
                        }
                    }
                } else {
                    if (this.$refs[`iteration_${index}`][0].validate()) {
                        let body = {
                            name: iteration.name,
                            clean: iteration.clean,
                            sql: iteration.sql,
                            configs: JSON.stringify(iteration.configs),
                            savedQueryId: iteration.savedQueryId
                        };
                        axios.patch(`/test/${iteration.testConfigsId}/iteration/${iteration.id}`, body)
                            .then(response => {
                                if (response) {
                                    this.iterationsBackup = this.iterationsBackup.filter(item => item.id !== iteration.id);
                                    iteration.edit = 'SAVED';
                                }
                            });
                    }
                }
            },
            handleCancel(iteration, index) {
                this.$refs[`iteration_${index}`][0].reset();
                if (iteration.edit === 'CREATE') {
                    this.iterations = this.iterations.filter((item, i) => i !== index);
                } else {
                    let iterationCandidate = this.iterations.find(item => item.id === iteration.id);
                    let iterationBackup = this.iterationsBackup.find(item => item.id === iteration.id);
                    iterationCandidate.id = iterationBackup.id;
                    iterationCandidate.name = iterationBackup.name;
                    iterationCandidate.testConfigsId = iterationBackup.testConfigsId;
                    iterationCandidate.userId = iterationBackup.userId;
                    iterationCandidate.sql = iterationBackup.sql;
                    iterationCandidate.clean = iterationBackup.clean;
                    iterationCandidate.databaseId = iterationBackup.databaseId;
                    iterationCandidate.savedQueryId = iterationBackup.savedQueryId;
                    for (let configName of Object.keys(iterationBackup.configs)) {
                        iterationCandidate.configs[configName] = iterationBackup.configs[configName];
                    }
                    iteration.edit = 'SAVED';
                }
            },
            // eslint-disable-next-line no-unused-vars
            handleEdit(iteration, i) {
                iteration.edit = 'EDIT';
                this.iterationsBackup.push(JSON.parse(JSON.stringify(iteration)));
            },
            closeDialog() {
                this.dialog = false;
                this.select = {};
                this.testModeConfig = {};
                this.testConfigs = {};
                this.stepNumber = 1;
                this.update = false;
                this.webhooksDeleted = [];
                this.$refs.databases.reset();
                this.$refs.testMode.reset();
            },
            openDialog() {
                this.dialog = true;
                this.$set(this.select, 'webhookModels', []);
                this.databasesLoading = true;
                this.fetchDatabases().then(() => {
                    databaseStatusFetch();
                    this.databasesLoading = false;
                })
            },
            getDatabaseById(id) {
                return this.getDatabases.find(database => database.id === id);
            },
            configureTest() {
                if (this.$refs.databases.validate()) {
                    this.stepNumber = 2;
                }
            },
            configureTestConfigs() {
                if (this.$refs.testConfigs.validate()) {
                    this.stepNumber = 3;
                }
            },
            configureSchedule() {
                if (this.$refs.testMode.validate()) {
                    this.stepNumber = 4;
                }
            },
            createTest() {
                if (this.$refs.schedule.validate()) {
                    Object.keys(this.getTestConfigs).forEach(key => {
                        if (this.getTestConfigs[key].inputType === 'switch' && !this.testConfigs[key]) {
                            this.testConfigs[key] = false;
                        }
                    });
                    Object.keys(this.getTestModeFields).forEach(key => {
                        if (this.getTestModeFields[key].inputType === 'switch' && !this.testModeConfig[key]) {
                            this.testModeConfig[key] = false;
                        }
                    });
                    this.webhooksDeleted.forEach(index => this.select.webhookModels.splice(index, 1));
                    if (this.select.webhookModels){
                        this.select.webhookModels.forEach(model => model.configs = JSON.stringify(model.configs));
                    } else {
                        this.select.webhookModels = [];
                    }
                    let body = {
                        databaseId: this.select.databaseId,
                        name: this.select.name,
                        testMode: testModeManager.getByName(this.select.testMode).key,
                        testModeConfigs: JSON.stringify(this.testModeConfig),
                        configs: JSON.stringify(this.testConfigs),
                        cron: this.select.cron,
                        webhookModels: this.select.webhookModels
                    };
                    if (this.update) {
                        axios.patch('/test/' + this.select.id, body).then(response => {
                            if (response.data) {
                                let test = this.tests.find(candidate => candidate.id === this.select.id);
                                test.id = response.data.id;
                                test.userId = response.data.userId;
                                test.databaseId = response.data.databaseId;
                                test.workerId = response.data.workerId;
                                test.name = response.data.name;
                                test.databaseType = response.data.databaseType;
                                test.testMode = response.data.testMode;
                                test.configs = response.data.configs;
                                test.testModeConfigs = response.data.testModeConfigs;
                                test.state = response.data.state;
                                test.lastExecutionId = response.data.lastExecutionId;
                                test.process = response.data.process;
                                test.cron = response.data.cron;
                                test.webhookModels = response.data.webhookModels;
                                if (test.webhookModels) {
                                    test.webhookModels.forEach(model => model.configs = JSON.parse(model.configs));
                                } else {
                                    test.webhookModels = [];
                                }
                                this.closeDialog();
                            }
                        });
                    } else {
                        axios.post('/test', body).then(response => {
                            if (response.data) {
                                this.tests.unshift(response.data);
                                response.data.webhookModels.forEach(model => model.configs = JSON.parse(model.configs));
                                this.closeDialog();
                            }
                        });
                    }
                }
            },
            deleteTest() {
                axios.delete("/test/" + this.deleteCandidate.id)
                    .then(response => {
                        if (response) {
                            this.tests = this.tests.filter(element => element !== this.deleteCandidate);
                            this.setDeleteDialog(false, null);
                            this.panel = undefined;
                        }
                    })
            },
            deleteIteration(iteration, index, testId) {
                axios.delete(`/test/${testId}/iteration/${iteration.id}`)
                    .then(response => {
                        if (response) {
                            this.iterations = this.iterations.filter((element, i) => i !== index);
                        }
                    })
            },
            setDeleteDialog(deleteDialog, deleteCandidate) {
                this.deleteDialog = deleteDialog;
                this.deleteCandidate = deleteCandidate;
            },
            createIteration(test) {
                let configs = {};
                let iterationConfigs = dbManager.findDbByType(test.databaseType).iterationConfigs;
                for (let configName of Object.keys(iterationConfigs)) {
                    configs[configName] = iterationConfigs[configName].defaultValue;
                }
                let createdIteration = {
                    edit: 'CREATE',
                    testConfigsId: test.id,
                    userId: test.userId,
                    databaseId: test.databaseId,
                    name: 'New Iteration',
                    clean: false,
                    sql: '',
                    databaseType: test.databaseType,
                    savedQueryId: 0,
                    configs
                };
                this.iterations.push(createdIteration);
            },
            editTest(test) {
                this.fetchDatabases().then(() => {
                    databaseStatusFetch();
                    this.select = {
                        name: test.name,
                        databaseId: test.databaseId,
                        testMode: testModeManager.getByKey(test.testMode).name,
                        id: test.id,
                        cron: test.cron,
                        webhookModels: test.webhookModels
                    };
                    this.testConfigs = JSON.parse(test.configs);
                    this.testModeConfig = JSON.parse(test.testModeConfigs);
                    this.update = true;
                    this.dialog = true;
                })
            },
            openExecutions(test) {
                router.push(`/tests/${test.id}/executions`)
            },
            startTest(test) {
                axios.post("/execution/start/test/" + test.id).then(response => {
                    if (response) {
                        test.lastExecutionId = response.data.id;
                        test.state = response.data.state;
                        //   this.buttonIcon = testState[execution.state].buttonIcon;
                        this.subscribe(test);
                    }
                })
            },
            subscribe(test) {
                test.subscription = stompClient.subscribe(`/test/${test.id}/execution/${test.lastExecutionId}`, (updated) => {
                    let body = JSON.parse(updated.body);
                    test.state = body.state;
                    test.process = body.process;
                    if (testState[test.state].buttonIcon.includes('START')) {
                        test.subscription.then(sub => sub.unsubscribe());
                        test.processSubscription.then(sub => sub.unsubscribe());
                        test.subscription = null;
                        test.processSubscription = null;
                    }
                }, {'id-sub': this.subId++});
                test.processSubscription = stompClient.subscribe(`/test/${test.id}/execution/${test.lastExecutionId}/process`, (updated) => {
                    let body = JSON.parse(updated.body);
                    test.process = body.process;
                }, {'id-sub': this.subId++});
            },
            setTestState(test, endpoint) {
                axios.post(endpoint + test.lastExecutionId).then(response => {
                    if (response) {
                        test.state = 'PENDING';
                    }
                });
            },
            getWorkerAlias(item) {
                let worker = this.getWorkerById(item.workerId);
                return worker ? worker.alias : '';
            },
            selectWebhook(index) {
                this.dialogWebhook = true;
                this.webhooksUpdate = true;
                this.webhooksModelCandidate = JSON.parse(JSON.stringify(this.select.webhookModels[index]));
                this.webhooksModelIndex = index;
            },
            closeWebhookDialog() {
                this.webhooksUpdate = false;
                this.dialogWebhook = false;
                this.webhooksModelIndex = null;
            },
            saveWebhookDialog() {
                if (this.webhooksUpdate) {
                    this.select.webhookModels[this.webhooksModelIndex].configs = this.webhooksModelCandidate.configs;
                    this.select.webhookModels[this.webhooksModelIndex].id = this.webhooksModelCandidate.id;
                    this.select.webhookModels[this.webhooksModelIndex].userId = this.webhooksModelCandidate.userId;
                    this.select.webhookModels[this.webhooksModelIndex].testId = this.webhooksModelCandidate.testId;
                    this.select.webhookModels[this.webhooksModelIndex].alias = this.webhooksModelCandidate.alias;
                    this.select.webhookModels[this.webhooksModelIndex].webhookType = this.webhooksModelCandidate.webhookType;
                } else {
                    this.select.webhookModels.push(this.webhooksModelCandidate)
                }
                this.webhooksModelCandidate = {};
                this.dialogWebhook = false;
                this.webhooksUpdate = false;
            },
            createWebhook() {
                this.dialogWebhook = true;
                this.webhooksUpdate = false;
                this.webhooksModelCandidate.configs = {};
            },
            getSavedQueries(){
                this.queriesLoading = true;
                axios.get("/sqllab").then(response => {
                    if (response.data) {
                        this.savedQueries = response.data.reverse();
                        this.savedQueries.unshift({id: 0, name: "Disable"})
                        this.queriesLoading = false;
                    }
                })
            }
        },
        computed: {
            ...mapGetters(["getWorkers"]),
            ...mapGetters(["getDatabases"]),
            getDatabasesIds() {
                return this.getDatabases.map(database => database.id);
            },
            getSavedQueriesIds(){
                return this.savedQueries.map(query => query.id);
            },
            getTestModes() {
                return testModeManager.getNames();
            },
            getTestModeFields() {
                if (this.select.testMode) {
                    return testModeManager.getByName(this.select.testMode).fields;
                } else return [];
            },
            getTestConfigs() {
                if (this.select.databaseId) {
                    let database = this.getDatabases.find(db => db.id === this.select.databaseId);
                    let dbType = dbManager.findDbByType(database.type);
                    return dbType.configs;
                }
                return {};
            },
            getNextDate() {
                if (this.select.cron) {
                    const interval = cronParser.parseExpression(this.select.cron);
                    return interval.next().toString();
                } else return null;
            },
            showWebhooks(){
                if (!this.select.webhookModels){
                    return false;
                }
                let array = this.getFilteredWebhooks;
                return array && array.length;
            },
            getFilteredWebhooks(){
                return this.select.webhookModels.filter((model, i) => !this.webhooksDeleted.includes(i));
            }
        },
        watch: {
            panel(testIndex) {
                const test = this.tests[testIndex];
                if (test) {
                    this.getIterationsByTest(test.id);
                }
            },
            'select.databaseId': function (newVal, oldVal) {
                if (newVal !== oldVal && !!oldVal) {
                    this.testConfigs = {};
                }
            },
            'select.testMode': function (newVal, oldVal) {
                if (newVal !== oldVal && !!oldVal) {
                    this.testModeConfig = {};
                }
            },
            // 'selectedWebhook' : function () {
            //     this.selectedWebhook = undefined;
            // }
            // iterations: {
            //     deep: true,
            //     handler: function(newValue) {
            //         newValue.sqlHtml = sqlhighlight(newValue.sql);
            //     }
            // }
        }
    }
</script>

<style scoped>
    .webhook-active {
        background-color: transparent;
    }
</style>
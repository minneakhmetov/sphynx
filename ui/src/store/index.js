import Vue from 'vue'
import Vuex from 'vuex'
import Alert from './alert';
import Auth from './auth';
import Worker from './worker';
import Database from './database';

Vue.use(Vuex);

export default new Vuex.Store({
  modules: {
    Alert, Auth, Worker, Database
  }
})

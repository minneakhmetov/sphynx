import Vue from 'vue'
import VueRouter from 'vue-router'
import FormLogin from '../components/FormLogin.vue'
import MainView from '../components/MainView.vue'
import DatabaseView from '../components/DatabaseView.vue'
import TestView from '../components/TestView.vue'
import WorkerView from '../components/WorkerView.vue'
import ExecutionView from '../components/ExecutionView.vue'
import MetricsView from '../components/MetricsView.vue'
import UserView from '../components/UserView.vue'
import SQLLabView from '../components/SQLLabView.vue'
import store from "../store";

Vue.use(VueRouter);

const routes = [
    {
        path: '/',
        redirect: '/main',
        meta: {
            auth: true
        }
    },
    {
        path: '/main',
        name: 'MainView',
        component: MainView,
        meta: {
            auth: true
        }
    },
    {
        path: '/database',
        name: 'DatabaseView',
        component: DatabaseView,
        meta: {
            auth: true
        }
    },
    {
        path: '/sqllab',
        name: 'SQLLabView',
        component: SQLLabView,
        meta: {
            auth: true
        }
    },
    {
        path: '/tests',
        name: 'TestView',
        component: TestView,
        meta: {
            auth: true
        }
    },
    {
        path: '/tests/:id/executions',
        name: 'ExecutionView',
        component: ExecutionView,
        meta: {
            auth: true
        }
    },
    {
        path: '/tests/:testId/executions/:id',
        name: 'MetricsView',
        component: MetricsView,
        meta: {
            auth: true
        }
    },
    {
        path: '/users',
        name: 'UserView',
        component: UserView,
        meta: {
            auth: true
        }
    },
    {
        path: '/workers',
        name: 'WorkerView',
        component: WorkerView,
        meta: {
            auth: true
        }
    },
    {
        path: '/login',
        name: 'Login Page',
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        //  component: () => import(/* webpackChunkName: "about" */ '../views/About.vue')
        component: FormLogin
    }
];

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
});

router.beforeEach((to, from, next) => {
    store.dispatch('getUserAuth').then(claims => {
        const requireAuth = to.matched.some(record => record.meta.auth);
        if (requireAuth && !claims) {
            next("/login");
        } else if (!requireAuth && claims) {
            next("/main");
        } else next();
    })
});

export default router

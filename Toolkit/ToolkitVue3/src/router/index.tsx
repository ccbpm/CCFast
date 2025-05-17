import { createRouter, createWebHashHistory } from 'vue-router';
import LoginView from '../views/Login.vue';
import IndexView from '../views/Index.vue';
import Error404 from '@/views/error/404.vue';
// import CCMobilePortal from '@/router/mobile';
// import CCMobileIndexView from '@/views/CCMobilePortal/page/Index.vue';

import { REDIRECT_NAME } from './constant';
export const menuRoutes = [
    {
        path: '/CarManage',
        name: 'CarManage',
        meta: {
            icon: 'icon-people',
            label: '车辆管理',
            enable: 1,
        },
        component: IndexView,
        children: [{
            path: 'UseKeep',
            name: 'UseKeep',
            meta: {
                icon: 'icon-user',
                title: '用车申请',
                label: '用车申请',
            },
            component: () => import('@/views/CarManager/DtlAth.vue')
        },
        {
            path: 'UseCar',
            name: 'Usecar',
            meta: {
                icon: 'icon-user',
                title: '历史维修',
                label: '历史维修',
            },
            component: () => import('@/views/CarManager/List.vue')
        },
        ]
    },
    {
        path: '/PactManage',
        name: 'PactManage',
        meta: {
            icon: 'icon-layers',
            label: '合同管理',
            enable: 1,
        },
        component: IndexView,
        children: [ {
            path: 'HeTong',
            name: 'HeTong',
            meta: {
                icon: 'icon-user',
                title: '合同审批',
                label: '合同审批',
            },
            component: () => import('@/views/CarManager/HeTong.vue')
        },
        ]
    },
    {
        path: '/Common',
        name: 'Common',
        meta: {
            icon: 'icon-drop',
            label: '功能页面集成',
            enable: 0,
        },
        component: IndexView,
        children: [{
            path: 'Port',
            name: 'Page',
            meta: {
                icon: 'icon-drop',
                label: 'port集成页面',
                title: 'port集成页面',
            },
            component: () => import('@/views/portPage/CommonPort.vue')
        },
        ]
    },
    {
        path: '/Home',
        name: 'home',
        meta: {
            icon: 'icon-drop',
            label: '门户页面',
            enable: 0,
        },
        component: IndexView,
        redirect: '/Home/Page',
        children: [{
            path: 'Page',
            name: 'page',
            meta: {
                icon: 'icon-drop',
                label: '首页',
                title: '首页',
            },
            component: () => import('@/views/portPage/HomePage.vue')
        },
        ]
    },
    {
        path: '/Port',
        name: 'Port',
        meta: {
            icon: 'icon-drop',
            label: '功能表格',
            enable: 0,
        },
        component: IndexView,
        children: [{
            path: 'Table',
            name: 'table',
            meta: {
                icon: 'icon-drop',
                label: '功能API',
                title: '功能API',
            },
            component: () => import('@/views/portPage/PortTable.vue')
        },
        ]
    },
    {
        path: '/Api',
        name: 'api',
        meta: {
            icon: 'icon-puzzle',
            label: '二开接口',
            enable: 1,
        },
        component: IndexView,
        children: [{
            path: 'apiList',
            name: 'apilist',
            meta: {
                icon: 'icon-layers',
                label: 'Toolkit接口',
                title: 'Toolkit接口',
            },
            component: () => import('@/views/portPage/ApiList.vue')
        },
        {
            path: 'ResfullList',
            name: 'resfullList',
            meta: {
                icon: 'icon-puzzle',
                title: '原始Resutfll接口',
                label: '原始Resutfll接口',
            },
            component: () => import('@/views/api/List.vue')
        },
        {
            path: 'Table',
            name: 'table',
            meta: {
                icon: 'icon-drop',
                label: '功能API',
                title: '功能API',
            },
            component: () => import('@/views/portPage/PortTable.vue')
        },
        {
            path: 'ApiCase',
            name: 'apicase',
            meta: {
                icon: 'icon-drop',
                label: 'API测试',
                title: 'API测试',
            },
            component: () => import('@/views/CarManager/FlowAppDemo.vue')
        },
        ]
    },
    {
        path: '/FixAsset',
        name: "FixAsset",
        meta: {
            icon: 'icon-user',
            label: '流程应用演示',
        },
        component: IndexView,
        children: [
            // {
            //     path: 'UseKeep',
            //     name: 'UseKeep',
            //     meta: {
            //         icon: 'icon-user',
            //         title: '车辆维修',
            //         label: '车辆维修',
            //     },
            //     component: () => import('@/views/CarManager/DtlAth.vue')
            // },
            // {
            //     path: 'UseCar',
            //     name: 'Usecar',
            //     meta: {
            //         icon: 'icon-user',
            //         title: '车辆台账',
            //         label: '车辆台账',
            //     },
            //     component: () => import('@/views/CarManager/List.vue')
            // },
            // {
            //     path: 'HeTong',
            //     name: 'HeTong',
            //     meta: {
            //         icon: 'icon-user',
            //         title: '合同审批',
            //         label: '合同审批',
            //     },
            //     component: () => import('@/views/CarManager/HeTong.vue')
            // },
            {
                path: 'Frame',
                name: 'Frame',
                meta: {
                    icon: 'icon-drop',
                    title: '嵌入式',
                    label: '嵌入式',
                },
                component: () => import('@/views/CarManager/frame.vue')
            },
            {
                path: 'QingJia',
                name: 'qingjia',
                meta: {
                    icon: 'icon-drop',
                    title: '请假流程',
                    label: '请假流程',
                },
                component: () => import('@/views/CarManager/QingJia.vue')
            },
            {
                path: 'MySetting',
                name: 'mySetting',
                meta: {
                    icon: 'icon-drop',
                    title: '我的设置',
                    label: '我的设置',
                },
                component: () => import('@/views/CarManager/MySetting.vue')
            },
        ]
    },
    {
        path: '/BillEntity',
        name: "billentity",
        meta: {
            icon: 'icon-user',
            label: '单据与实体',
        },
        component: IndexView,
        children: [
            {
                path: 'Student',
                name: 'student',
                meta: {
                    icon: 'icon-user',
                    title: '学生实体',
                    label: '学生实体',
                },
                component: () => import('@/views/Entity/Student.vue')
            },
            {
                path: 'PayBill',
                name: 'paybill',
                meta: {
                    icon: 'icon-user',
                    title: '缴费单据',
                    label: '缴费单据',
                },
                component: () => import('@/views/Entity/PayBill.vue')
            },
            {
                path: 'MenuApi',
                name: 'menuapi',
                meta: {
                    icon: 'icon-user',
                    title: '菜单API',
                    label: '菜单API',
                },
                component: () => import('@/views/Entity/MenuApi.vue')
            },
        ]
    },
    // {
    //         path: '/CCMobilePortal',
    //         name: 'CCMobilePortal',
    //         component: CCMobileIndexView,
    //         // redirect: '/CCMobilePortal/Home',
    //         meta: {
    //             title: '移动端',
    //             enable: 0,
    //         },
    //         children: [
    //             //   {
    //             //     path: 'Login',
    //             //     name: 'CCMobilePortalLogin',
    //             //     component: () => import('/src/CCMobilePortal/Login.vue'),
    //             //     meta: {
    //             //       title: '首页',
    //             //       ignoreKeepAlive: true,
    //             //     },
    //             //   },
    //             {
    //                 path: 'Home',
    //                 name: 'CCMobilePortalHome',
    //                 component: () => import('@/views/CCMobilePortal/Home.vue'),
    //                 meta: {
    //                     title: '首页',
    //                     label: '首页',
    //                     ignoreKeepAlive: true,
    //                 },
    //             },
    //         ],
    //     }
]
const staticRoutes = [
    {
        path: '/',
        name: 'Index',
        redirect: '/Home/Page'
    },
    {
        path: '/login',
        name: 'login',
        component: LoginView,
        meta: {
            title: 'ToolKit 登录',
        }
    },
    {
        path: '/:path(.*)*',
        name: 'error',
        component: Error404,
    },
    {
        path: '/redirect',
        component: IndexView,
        name: 'RedirectTo',
        meta: {
            title: REDIRECT_NAME,
            hideBreadcrumb: true,
            hideMenu: true,
        },
        children: [
            {
                path: '/redirect/:path(.*)',
                name: REDIRECT_NAME,
                component: () => import('@/views/redirect/index.vue'),
                meta: {
                    title: REDIRECT_NAME,
                    hideBreadcrumb: true,
                },
            },
        ],
    }

]

const routes = [...menuRoutes, ...staticRoutes]
export const router = createRouter({
    history: createWebHashHistory(import.meta.env.BASE_URL),
    routes
})


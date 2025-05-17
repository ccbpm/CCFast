<template>
     <div class="quick-links">
        <Menu mode="horizontal" v-model:selectedKeys="selectedKeys" style="align-items: center; background: var(--system-bg-color); height: 48px; font-size: 14px">
            <menu-item class="menu-item" key="110-01" @click="addTabByMenu({ path: '/Common/Port', title: '发起', Paras: '?DoWhat=Component&EnName=GL_Start&Name=发起' })">
              <i class="icon-paper-plane"></i>
              发起
            </menu-item>
            <menu-item class="menu-item" key="110-02" @click="addTabByMenu({ path: '/Common/Port', title: '待办', Paras: '?DoWhat=Component&EnName=GL_Todolist&Name=待办' })">
              <Badge :count="todoNum" :number-style="numberStyle" :overflow-count="99">
                <i class="icon-clock"></i>
                待办
              </Badge>
            </menu-item>

            <menu-item class="menu-item" key="110-04" @click="addTabByMenu({ path: '/Common/Port', title: '在途', Paras: '?DoWhat=Component&EnName=GL_Runing&Name=在途' })">
              <i class="icon-hourglass"></i>
              在途
            </menu-item>
            <menu-item class="menu-item" key="110-05" @click="addTabByMenu({ path: '/Common/Port', title: '近期', Paras: '?DoWhat=Component&EnName=GL_RecentWork&Name=近期' })">
              <i class="icon-envelope"></i>
              近期
            </menu-item>
            <menu-item class="menu-item" key="110-06" @click="addTabByMenu({ path: '/Common/Port', title: '抄送', Paras: '?DoWhat=Component&EnName=GL_CC&Name=抄送' })">
              <i class="icon-bag"></i>
              抄送
            </menu-item>
        </Menu>
        <Menu mode="horizontal" v-model:selectedKeys="selectedKeys" style="align-items: center;justify-content: flex-end; background: var(--system-bg-color); height: 48px; font-size: 14px">
              <menu-item class="menu-item" key="999-02" @click="addTabByMenu({ path: '/Home/Page', title: '首页', Paras: '' })">
                <i class="icon-pie-chart"></i>
                首页
              </menu-item>
          <template v-if="isAdmin">
              <!-- <menu-item class="menu-item" key="999-03" @click="addTabByMenu({ path: '/Port/Table', title: '功能API', Paras: '' })">
                <i class="icon-layers"></i>
                功能API
              </menu-item> -->
              <menu-item class="menu-item" key="999-04" @click="addTabByMenu({ path: '/Common/Port', title: '流程', Paras: '?DoWhat=Component&EnName=TreeEns_FlowSort2Flow&Name=流程' })">
                <i class="icon-organization"></i>
                流程
              </menu-item>
              <menu-item class="menu-item" key="999-05" @click="addTabByMenu({ path: '/Common/Port', title: 'Port表单', Paras: '?DoWhat=Component&EnName=TreeEns_FrmSort2Frm&Name=表单' })">
                <i class="icon-diamond"></i>
                表单
              </menu-item>
              <menu-item class="menu-item" key="999-06" @click="addTabByMenu({ path: '/Common/Port', title: '组织', Paras: '?DoWhat=Component&EnName=TreeEns_Dept2Emp&Name=组织' })">
                <i class="icon-people"></i>
                组织
              </menu-item>
              <menu-item class="menu-item" key="999-07" @click="addTabByMenu({ path: '/Common/Port', title: '数据源', Paras: '?DoWhat=Component&EnName=TreeEns_DBSrc&Name=数据源' })">
                <i class="icon-settings"></i>
                数据源
              </menu-item>
              <menu-item class="menu-item" key="999-08" @click="addTabByMenu({ path: '/Common/Port', title: '消息', Paras: '?DoWhat=Component&EnName=GL_Msg&Name=消息' })">
                <Badge :count="msgNum" :number-style="numberStyle" :overflow-count="99">
                  <i class="icon-bubbles"></i>
                  消息
                </Badge>
              </menu-item>
            </template>
        </Menu>
    </div>
</template>
  <script lang="ts" setup>
  import { computed, ref, unref } from 'vue';
  import { Menu, MenuItem, Badge } from 'ant-design-vue';
  import { useRouter, type RouteLocationNormalized, type RouteMeta } from 'vue-router';
  import { listenerRouteChange } from '@/logics/mitt/routeChange'
  import { REDIRECT_NAME } from '@/router/constant';
  import { useUserStore } from '@/stores/user';
  import { useMultipleTabStore } from '@/stores/multiTabs';
  import Dev2ApiInterface from '@/Toolkit/Dev2ApiInterface';


    const selectedKeys = ref<string[]>([]);
    const userStore = useUserStore();
    const router = useRouter();
    const tabStore = useMultipleTabStore();
    const todoNum = ref();
    const msgNum = ref();
    listenerRouteChange((route: any) => {
      const { name } = route;
      if (name === REDIRECT_NAME || !route || !userStore.getToken) {
        return;
      }

      const { path, meta = {} } = route;
      selectedKeys.value = [path];
      const { currentActiveMenu, hideTab } = meta as RouteMeta;
      const isHide = !hideTab ? null : currentActiveMenu;
      if (isHide) {
        const findParentRoute = router.getRoutes().find((item) => item.path === currentActiveMenu);

        findParentRoute && tabStore.addTab(findParentRoute as unknown as RouteLocationNormalized);
      } else {
        tabStore.addTab(unref(route));
      }
    });

  const isAdmin = computed(() => {
    return userStore.webUser.No === 'admin';
  });

  async function addTabByMenu(tabInfo: any) {
    console.log(tabInfo)
    router.push(tabInfo.path + tabInfo.Paras);
  }

  const numberStyle = computed(() => ({
    top: '-10px',
    width: '30px',
    height: '15px',
    lineHeight: '15px',
  }));

  const InitPage =()=>{
    Dev2ApiInterface.Number_Todolist().then(res=>{
      todoNum.value = res
      console.log('todoNum',todoNum.value);
    });
    Dev2ApiInterface.Number_Message().then(res=>{
      msgNum.value = res
      console.log('msgNum',msgNum.value);
    });

  }
  InitPage();

  </script>
  
  <style lang="less" scoped>
    .quick-links {
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex: 1;
      padding-top: 1px; //添加
      :deep(.ant-menu) {
        flex: 1;
        :deep(.ant-menu-item:hover::after) {
          background-color: #0c46ae;
          color: #fff;
        }
      }
      :deep(.ant-menu-light) {
        .menu-item {
          padding: 0 8px;
        }
      }
  }
  </style>
  
<script lang="tsx">
import { defineComponent, onBeforeUnmount, onMounted, ref, Transition, unref, watch } from 'vue'
import {
  Layout,
  LayoutSider,
  Menu,
  SubMenu,
  MenuItem,
  LayoutHeader,
  LayoutContent,
  message
} from 'ant-design-vue'
import { RouterView, type RouteRecordRaw, useRouter, type RouteMeta, type RouteLocationNormalized, useRoute } from 'vue-router'
import LogoCollapsePic from '@/assets/logo_collapse.png'
import LogoNormalPic from '@/assets/logo.png'
import { MenuUnfoldOutlined, MenuFoldOutlined } from '@ant-design/icons-vue'
import { menuRoutes } from '@/router'
import UserDropdownVue from '@/components/UserDropdown.vue'
import LayoutMenu from '@/components/LayoutMenu.vue'
import HeaderTabs from '@/components/HeaderTabs.vue'
import { listenerRouteChange } from '@/logics/mitt/routeChange'
import { useUserStore } from '@/stores/user'
import { useMultipleTabStore } from '@/stores/multiTabs'
import { initAffixTabs } from '@/hooks/web/useMultipleTabs'
import { REDIRECT_NAME } from '@/router/constant'
import { useProjectStore } from '@/stores/projectSetting'
import { computed } from 'vue'
import  menuList  from '@/portal/menu';
import WebUser from '@/Toolkit/WebUser'

export default defineComponent({
  name: 'index-page',
  setup() {
    const collapsed = ref(false)
    const selectedKeys = ref<string[]>([])
    const tabStore = useMultipleTabStore();
    const router = useRouter();
    const route = useRoute();
    const userStore = useUserStore();
    const projectSetting = useProjectStore();

    //处理admin显示
    // 通用页面处理
    const updatedMenuList = menuList.map(item => {
    // 检查 GroupName 是否包含 'admin'
      const containsAdmin = item.GroupName.toLowerCase().includes('admin');
      // 如果包含 'admin'，则修改 IsShow 属性
      if (containsAdmin) {
        return {
          ...item, // 复制当前项的所有属性
          IsShow: WebUser.No == 'admin'? 1 : 0 //设置IsShow 的值
        };
      }
      // 如果不包含 'admin'，则返回原始项
      return item;
    });
    const menuListIsShow = updatedMenuList.filter((item) => item.IsShow === 1 );

    // 路由菜单处理
    const filterRoutesByRole = (menuList: any[]) => {
      // 如果用户是管理员，返回所有路由；否则，排除包含 'api' 的路由
      return menuList.filter(item => {
        return WebUser.No === 'admin' || !item.path.toLowerCase().includes('api');
      });
    };

    //end

    const headerShow = computed(() => {
      return projectSetting.getHeaderSetting.isShow;
    });
    const siderShow = computed(() => {
      return projectSetting.getSiderSetting.isShow;
    });
    const renderMenuItem = (item: RouteRecordRaw) => {
      const meta = item.meta as any
      if (!meta) return <p>请配置meta参数</p>
      if (Array.isArray(item.children) && item.children.length > 0 && meta.enable != 0) {
        return (
          <SubMenu
            key={item.path}
            v-slots={{
              title:() => <span><i class={meta.icon}></i> {meta.label}</span>,
            }}
          >
            {item.children?.map((child) => {
              const cMeta = child.meta as any
              if (!meta) return <p>请配置meta参数</p>
              return (
                <MenuItem
                  key={item.path + '/' + child.path}
                >
                  <i style={{ marginRight: '5px' }} class={cMeta.icon}></i> {cMeta.label}
                </MenuItem>
              )
            })}
          </SubMenu>
        )
      }
      // return (
      //   <MenuItem key={item.path}>
      //     <meta.icon />
      //     {meta.label}
      //   </MenuItem>
      // )
    }


    //功能集成菜单
    const renderPortMenuItem = (item: {
       GroupName: string; Icon: string; Path: string; children: { No: string; Name: string; FileUrl: string; Path: string; Paras: string; Icon: string; Enable: number }[] 
}) => {
      // console.log('item',item);
      if (Array.isArray(item.children) && item.children.length > 0) {
        return (
          <SubMenu
            key={item.GroupName}
            v-slots={{
              title:() => <span><i  class={item.Icon}></i> {item.GroupName}</span>,
            }}
          >
            {item.children?.map((child) => {
              return (
                <MenuItem
                  key={item.Path + '/' + child.Path+'?'+child.Paras+'&Name='+child.Name}
                >
                 <i style={{ marginRight: '5px' }} class={child.Icon}></i> {child.Name}
                </MenuItem>
              )
            })}
          </SubMenu>
        )
      }
      // return (
      //   <MenuItem key={item.Path}>
      //     <item.Icon />
      //     {item.GroupName}
      //   </MenuItem>
      // )
    }




    initAffixTabs();

    const onMenuSelect = (item: any) => {
      console.log(item);
      router.push(item.key);
    }
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
    const getTabsState = computed(() => {
      return tabStore.getTabList.filter((item: any) => !item.meta?.hideTab);
    }); 
   // 添加事件监听器来接收消息
   const handleMessage = (event: MessageEvent) => {
      if (event.data?.type === 'logout' && event.data.msg === true) {
        console.log('退出登录');
        message.info('登录过期，已退出，请重新登录');
        userStore.logout();
      }
      if (event.data?.type === 'close-page' && event.data.msg === true) {
        console.log('关闭单流程页面');
        const unClose = computed(() => unref(getTabsState).length === 1);
        if (unref(unClose)) {
          return;
        }
        let targetKey = ''
        if(location.search){
          targetKey = decodeURIComponent(location.pathname + location.search);
        }else{
          targetKey = route.query ? route.fullPath : route.path;
        }
        tabStore.closeTabByKey(targetKey, router);
      }
    };
    // 在组件挂载时添加事件监听器
    onMounted(async () => {
      window.addEventListener('message', handleMessage, true);
      await filterRoutesByRole(menuRoutes);
    });
 
    // 在组件卸载前移除事件监听器
    onBeforeUnmount(() => {
      window.removeEventListener('message', handleMessage, true);
    });
 
    return () => (
      <Layout style={{ height: '100vh' }}>
        {
          siderShow.value == true ? (
            <LayoutSider v-if = {siderShow.value} v-model:collapsed={collapsed.value} trigger={null} collapsible>
              <div class="logo">
                <img src={collapsed.value ? LogoCollapsePic : LogoNormalPic} alt="logo" />
              </div>
              <Menu
                v-model:selectedKeys={selectedKeys.value}
                theme="dark"
                mode="inline"
                onSelect={onMenuSelect}
              >
                {menuListIsShow.map((menu) => renderPortMenuItem(menu))}
              </Menu>
              <Menu
                v-model:selectedKeys={selectedKeys.value}
                theme="dark"
                mode="inline"
                onSelect={onMenuSelect}
              >
                {filterRoutesByRole(menuRoutes).map((menu) => renderMenuItem(menu))}
              </Menu>
            </LayoutSider>
          ):(null)
        }
        <Layout>
          {
            headerShow.value == true ? (
              <LayoutHeader style={{ background: '#fff', padding: 0, height: '48px' }}>
                <div class="header-content">
                  {collapsed.value ? (
                    <MenuUnfoldOutlined
                      class="trigger"
                      onClick={() => (collapsed.value = !collapsed.value)}
                    />
                  ) : (
                    <MenuFoldOutlined
                      class="trigger"
                      onClick={() => (collapsed.value = !collapsed.value)}
                    />
                  )}
                  <LayoutMenu />
                  <UserDropdownVue />
                </div>
              </LayoutHeader>
            ):(null)
          }
          <HeaderTabs></HeaderTabs>
          <div style={{height:'calc(100% - 35px - 48px)'}}>
            <LayoutContent
            style={{
              margin: '0px 5px',
              background: '#fff',
              height : '100%',
              overflow:'auto',
            }}
          >
            <RouterView />
          </LayoutContent>
          </div>
          
        </Layout>
      </Layout>
    )
  }
})
</script>

<style lang="less" scoped>
.logo {
  height: 48px;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;

  img {
    height: 36px;
    object-fit: contain;
  }
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-sizing: border-box;
  padding-right: 24px;
  height: 48px;
}

:deep(.trigger) {
  font-size: 18px;
  // line-height: 64px;
  padding: 0 12px;
  cursor: pointer;
  transition: color 0.3s;
  &:hover {
    color: #1890ff;
  }
}

.site-layout .site-layout-background {
  background: #fff;
}
</style>

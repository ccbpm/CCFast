<template>
  <div>
    <Dropdown>
      <div style="cursor: pointer">
          <UserOutlined style="margin-right: 12px" />
            {{userStore.webUser.Name}}
          <DownOutlined style="margin-left: 6px" />
      </div>
      <template #overlay>
          <Menu @click="onClick">
              <MenuItem key="todoc" >官网地址</MenuItem>
              <MenuItem key="logout" >退出登录</MenuItem>
              <!-- <MenuItem key="changeUser">切换用户</MenuItem> -->
          </Menu>
      </template>
    </Dropdown>
    <Modal
    v-model:visible="changeModal.visible"
    title="切换账号"
    width="300px"
    :body-style="{ padding: '24px' }"
    :footer="null"
  >
    <Form @submit.prevent="handleOk">
      <FormItem
        label="账号"
        name="username"
      >
        <Input v-model:value="changeModal.username" />
      </FormItem>
      <FormItem
        label="密码"
        name="password"
      >
        <Input
          v-model:value="changeModal.password"
          type="password"
        />
      </FormItem>
      <FormItem >
        <Button style="margin:0 40px;" type="primary" html-type="submit">切换</Button>
        <Button style="margin:0 20px;" @click="handleCancel">取消</Button>
      </FormItem>
    </Form>
    <div class="changeNo">
        <span>测试账号:</span>
        <span>admin 管理员, guobaogeng财务人员, liping人力资源部经理, zhoutianjiao开发部技术人员.</span>
    </div>
  </Modal>
  </div>
</template>
<script setup lang="ts">
import { reactive } from 'vue'
import { Dropdown, MenuItem, Menu, Modal, Form,FormItem, Input,Button  } from 'ant-design-vue'
import { useUserStore } from '@/stores/user'
import { DownOutlined, UserOutlined } from '@ant-design/icons-vue'

    const userStore = useUserStore()
    const logout = () => {
      userStore.confirmLoginOut();
    }
    const todoc = ()=>{
      // window.open('https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=8095471&doc_id=31094')
      window.open('https://ccflow.org/index.html')
    }
    const changeModal = reactive<Recordable>({
      title:'',
      visible:false,
      username:'',
      password:'',
      modalWidth:800,
      modalHeight: {
          height: '500px',
            },
      })
    const changeUser = () =>{
      changeModal.title='切换账号';
      changeModal.visible = true;
    }
      // 处理关闭模态框的函数
    const handleOk = async () => {
      changeModal.visible = false;
      const webUser = useUserStore()
      await webUser.login({
        username: changeModal.username,
        password: changeModal.password,
      })
      location.reload();
    };
  
    // 处理取消（或关闭）模态框的函数
    const handleCancel = () => {
      changeModal.visible = false;
    };

    const onClick =(res: any)=>{
      switch (res.key) {
        case 'logout':
          //退出登录
          logout()
          break;
        case 'todoc':
          //官网地址
          todoc()
          break;
        case 'changeUser':
          //切换用户
          changeUser()
          break;
      
        default:
          break;
      }
    }
</script>
<style lang="less" scoped>
  .changeNo{
    display: flex;
    flex-direction: column;
  }
</style>


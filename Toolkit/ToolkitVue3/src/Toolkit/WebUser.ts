import { useUserStore } from '@/stores/user';
import { reactive } from 'vue';

export interface User {
  No: string;
  Name: string;
  OrgNo: string;
  OrgName: string;
  FK_Dept: string;
  FK_DeptName: string;
  SysLang: string;
  CCBPMRunModel: number;
  IsAdmin: boolean | number;
  Token: string;
  homePath?: string;
  avatar?: string;
  IsFirstLogin: string;
  Roles: '';

  RootNo: '';
}

export interface TokenInfo {
  Token: string;
  UserNo: string;
  OrgNo: string;
}

class UserInfo {
  [x: string]: any;

  get No() {
    return this.user.No;
  }
  get Name() {
    return this.user.Name;
  }

  get DeptNo() {
    return this.user.FK_Dept;
  }

  get DeptName() {
    return this.user.FK_DeptName;
  }

  get OrgNo() {
    return this.user?.OrgNo;
  }

  get OrgName() {
    return this.user.OrgName;
  }

  get IsAdmin() {
    return !!this.user.IsAdmin;
  }

  get Token() {
    return this.user.Token;
  }

  get SysLang() {
    return this.user.SysLang;
  }

  get Avatar() {
    return this.user.avatar;
  }

  get CCBPMRunModel() {
    return parseInt(this.user.CCBPMRunModel || 0);
  }
  get user() {
    // if (this.userObj.No == '') {
      const userStore = useUserStore();
      this.userObj = userStore.webUser as User;
      return this.userObj;
    // }
    // return this.userObj;
  }
  get IsFirstLogin() {
    return this.user.IsFirstLogin;
  }
  get Roles() {
    //逗号分隔角色id
    return this.user.Roles;
  }
  get RootNo() {
    return this.user.RootNo;
  }
  set setIsFirstLogin(value: string | undefined) {
    this.user.IsFirstLogin = value;
  }
  private userObj: Partial<User> = {
    No: '',
    Name: '',
    FK_Dept: '',
    FK_DeptName: '',
    OrgName: '',
    OrgNo: '',
    IsAdmin: false,
    Token: '',
    avatar: '',
    CCBPMRunModel: 0,
    SysLang: 'CH',
    IsFirstLogin: '0',
    Roles: '',
  };

  set userInfo(user: User | null) {
    if (user) {
      this.userObj = user;
    } else {
      this.userObj = {
        No: '',
        Name: '',
        FK_Dept: '',
        FK_DeptName: '',
        OrgName: '',
        OrgNo: '',
        IsAdmin: false,
        Token: '',
        avatar: '',
        CCBPMRunModel: 0,
        SysLang: 'FT',
        IsFirstLogin: '0',
        Roles: '',
      };
    }
  }
}

const WebUser = reactive(new UserInfo());

export default WebUser;

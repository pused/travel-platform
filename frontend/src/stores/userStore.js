import { defineStore } from "pinia";

/**
 * 前后台会话完全分离：
 * - 前台：frontToken / frontUserInfo / frontRoleFlag
 * - 后台：adminToken / adminUserInfo / adminRoleFlag
 * 互不覆盖，刷新页面后保持独立。
 */
const useUserStore = defineStore("user", {
  state: () => ({
    frontToken: localStorage.getItem("frontToken") ?? "",
    frontUserInfo: localStorage.getItem("frontUserInfo")
      ? JSON.parse(localStorage.getItem("frontUserInfo"))
      : null,
    frontRoleFlag: localStorage.getItem("frontRoleFlag") ?? "",
    adminToken: localStorage.getItem("adminToken") ?? "",
    adminUserInfo: localStorage.getItem("adminUserInfo")
      ? JSON.parse(localStorage.getItem("adminUserInfo"))
      : null,
    adminRoleFlag: localStorage.getItem("adminRoleFlag") ?? "",
  }),
  getters: {
    isFrontLoggedIn: (state) => !!state.frontToken,
    isAdminLoggedIn: (state) => !!state.adminToken,
    roleFlag: (state) => state.adminRoleFlag || state.frontRoleFlag || "",
  },
  actions: {
    setFrontSession(token, userInfo, roleFlag) {
      this.frontToken = token;
      this.frontUserInfo = userInfo;
      this.frontRoleFlag = roleFlag;
      localStorage.setItem("frontToken", token || "");
      localStorage.setItem("frontRoleFlag", roleFlag || "");
      localStorage.setItem(
        "frontUserInfo",
        userInfo ? JSON.stringify(userInfo) : ""
      );
    },
    setAdminSession(token, userInfo, roleFlag) {
      this.adminToken = token;
      this.adminUserInfo = userInfo;
      this.adminRoleFlag = roleFlag;
      localStorage.setItem("adminToken", token || "");
      localStorage.setItem("adminRoleFlag", roleFlag || "");
      localStorage.setItem(
        "adminUserInfo",
        userInfo ? JSON.stringify(userInfo) : ""
      );
    },
    setFrontUserInfo(userInfo) {
      this.frontUserInfo = userInfo;
      localStorage.setItem(
        "frontUserInfo",
        userInfo ? JSON.stringify(userInfo) : ""
      );
    },
    setAdminUserInfo(userInfo) {
      this.adminUserInfo = userInfo;
      localStorage.setItem(
        "adminUserInfo",
        userInfo ? JSON.stringify(userInfo) : ""
      );
    },
    logoutFront() {
      this.frontToken = "";
      this.frontUserInfo = null;
      this.frontRoleFlag = "";
      localStorage.removeItem("frontToken");
      localStorage.removeItem("frontUserInfo");
      localStorage.removeItem("frontRoleFlag");
    },
    logoutAdmin() {
      this.adminToken = "";
      this.adminUserInfo = null;
      this.adminRoleFlag = "";
      localStorage.removeItem("adminToken");
      localStorage.removeItem("adminUserInfo");
      localStorage.removeItem("adminRoleFlag");
    },
    clearAll() {
      this.logoutFront();
      this.logoutAdmin();
    },
  },
  persist: {
    key: "user",
    storage: localStorage,
    paths: [
      "frontToken",
      "frontUserInfo",
      "frontRoleFlag",
      "adminToken",
      "adminUserInfo",
      "adminRoleFlag",
    ],
  },
});

export default useUserStore;

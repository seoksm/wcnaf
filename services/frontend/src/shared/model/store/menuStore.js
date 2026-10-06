import { create } from 'zustand';

const initialState = {
    currentMenuId: '',
};

const menuStore = create((set) => ({
    ...initialState,

    setCurrentMenuId: (menuId) => set({ currentMenuId: menuId }),

    reset: () => set({ ...initialState }),
}));

/** ====== 읽기 유틸 ====== */
menuStore.getCurrentMenuId = () => menuStore.getState().currentMenuId?.toString();

export default menuStore;

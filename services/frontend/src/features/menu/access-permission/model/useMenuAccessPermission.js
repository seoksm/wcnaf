import { useState, useEffect } from 'react';
import { winiCom } from '@/shared/lib';
import { winiMsg, userStore } from '@/shared/model';
import {
  fetchMenuAccessPermissions,
  batchCreateMenuAccessPermissions,
} from '../api/api';
import {
  calDepths,
  fnConfChkAll,
  setChild,
  fnSetChildAllChk,
  fnSetChildColChk,
} from './helpers';

/**
 * 메뉴 접근 권한 관리 로직
 */
export const useMenuAccessPermission = (authorizationGroupId) => {
  const { connector, id: currentMenuId } = winiCom.getFormInfo();

  const [menuGridDataList, setMenuGridDataList] = useState([]);

  /**
   * 메뉴별 권한 조회
   */
  const loadMenuPermissions = async (id) => {
    if (!id) {
      setMenuGridDataList([]);
      return;
    }

    try {
      const data = await fetchMenuAccessPermissions(connector, id);

      if (data.result === 'SUCCESS') {
        const menuAuthList = data.data;

        if (menuAuthList.length > 0) {
          const cpMenuAuthList = menuAuthList.map((obj) => ({ ...obj }));

          let afSetDepthObj = calDepths(cpMenuAuthList);

          afSetDepthObj.forEach((obj) => {
            obj.chkAll = fnConfChkAll(obj);
          });

          let map = {};
          let tree = [];

          afSetDepthObj.forEach((item) => {
            if (!item.parentAuthorizationGroupId) {
              item.parentAuthorizationGroupId = '';
            }
            map[item.id] = { ...item, children: [] };
          });

          afSetDepthObj.forEach((item) => {
            if (
              item.parentMenuId === null ||
              !item.parentMenuId ||
              item.parentMenuId === ''
            ) {
              tree.push(map[item.menuId]);
            } else {
              map[item.parentMenuId]?.children.push(map[item.menuId]);
            }
          });

          setChild(tree, afSetDepthObj);
          setMenuGridDataList(afSetDepthObj);
        }
      }
    } catch (error) {
      winiMsg.showAlert(winiCom.getErrorMessage(error.response.data.message));
    }
  };

  /**
   * 체크박스 변경 핸들러
   */
  const handleCheckboxChange = (params) => {
    const field = params.colDef.field;
    const allChkVal = params.newValue;
    const rowIndex = params.node.rowIndex;
    const newMenuGridDataList = [...menuGridDataList];

    newMenuGridDataList[rowIndex][field] = params.newValue ? 'ALLOW' : 'NONE';

    const rowData = params.data;
    const allChkYn = fnConfChkAll(rowData);
    rowData.chkAll = allChkYn;

    if (field === 'chkAll') {
      const rowKeys = Object.keys(rowData);
      const ynKeys = rowKeys.filter(
        (key) =>
          key.toLowerCase().slice(-6, key.length) === 'status' &&
          key.length > 6
      );

      ynKeys.forEach((item) => {
        if (allChkVal === true) {
          rowData.chkAll = 'ALLOW';
          rowData[item] = 'ALLOW';
        } else {
          rowData.chkAll = 'NONE';
          rowData[item] = 'NONE';
        }
      });

      if (rowData.hasOwnProperty('children') && rowData.children != null) {
        const childArr = rowData.children;
        fnSetChildAllChk(newMenuGridDataList, childArr, ynKeys, allChkVal);
      }
    } else {
      if (rowData.hasOwnProperty('children') && rowData.children != null) {
        fnSetChildColChk(
          newMenuGridDataList,
          rowData.children,
          field,
          allChkVal
        );
      }
    }

    setMenuGridDataList(newMenuGridDataList);
  };

  /**
   * 메뉴 권한 저장
   */
  const handleSave = async (authId) => {
    if (!authId) {
      await winiMsg.showAlert('권한을 선택해주세요');
      return;
    }

    const saveParams = [];
    const keys = Object.keys(menuGridDataList[0]);
    const saveKeys = keys.filter(
      (key) =>
        key.toLowerCase().slice(-6, key.length) === 'status' &&
        key.length > 6 &&
        key !== 'menuStatus'
    );

    menuGridDataList.forEach((each) => {
      const saveItem = {};
      saveKeys.forEach((item) => {
        saveItem[item] = each[item] || 'NONE';
        saveItem.menuId = each.menuId;
      });

      if (JSON.stringify(saveItem) !== '{}') {
        saveParams.push(saveItem);
      }
    });

    // 자기 잠금 방지 - 내가 속한 권한그룹을 수정하는 중이고, 그 저장 내용이 바로 이
    // 화면(메뉴접근권한)의 권한을 전부 NONE으로 만든다면, 저장 즉시 이 화면 자체에
    // 다시 들어올 수 없게 된다(복구 경로 없음). LNB가 화면 노출 여부를 판단하는 기준
    // (모든 *Status가 NONE이면 접근불가, AuthenticatedLayout.jsx의 isNoMenuPermission과
    // 동일한 조건)과 똑같이 판단한다.
    const isEditingOwnGroup = String(authId) === String(userStore.getGroupId());
    if (isEditingOwnGroup) {
      const ownMenuRow = saveParams.find((item) => String(item.menuId) === String(currentMenuId));
      const wouldSelfLock = ownMenuRow
        && Object.entries(ownMenuRow).every(([key, value]) => key === 'menuId' || value !== 'ALLOW');
      if (wouldSelfLock) {
        await winiMsg.showAlert(
          '본인이 속한 권한그룹의 이 화면(메뉴 접근권한) 접근권한을 전부 해제할 수 없습니다. '
          + '저장하면 이 화면에 다시 들어올 방법이 없어집니다.',
        );
        return;
      }
    }

    try {
      const data = await batchCreateMenuAccessPermissions(
        connector,
        authId,
        saveParams
      );
      if (data.result === 'SUCCESS') {
        winiMsg.showSnackbar('권한이 등록 되었습니다.');
      }
    } catch (error) {
      winiMsg.showAlert(winiCom.getErrorMessage(error.response.data.message));
    }
  };

  /**
   * authorizationGroupId 변경 시 메뉴 권한 조회
   */
  useEffect(() => {
    loadMenuPermissions(authorizationGroupId);
  }, [authorizationGroupId]);

  return {
    menuGridDataList,
    handleCheckboxChange,
    handleSave,
  };
};

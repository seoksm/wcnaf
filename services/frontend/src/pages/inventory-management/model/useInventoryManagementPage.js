import { useCallback } from 'react';
import { useInventoryList } from '@/features/inventory/list';
import { useCreateMemberInventory, useCreateAdminInventory } from '@/features/inventory/create';
import { useInventoryDetail } from '@/features/inventory/detail';
import { useInventoryReport } from '@/features/inventory/report';
import { useInventorySchedules } from '@/features/inventory/schedule';

export const useInventoryManagementPage = () => {
  const { inventoryList, pageInfo, isLoading: isListLoading, error: listError, fetchInventoryList } = useInventoryList();
  const detail = useInventoryDetail();
  const report = useInventoryReport();

  const refreshCurrentList = useCallback(
    () => fetchInventoryList({ page: pageInfo.currentPage }),
    [fetchInventoryList, pageInfo.currentPage],
  );

  const onPageChange = useCallback(
    async (nextPage) => {
      await fetchInventoryList({ page: nextPage });
    },
    [fetchInventoryList],
  );

  const onSelectInventory = useCallback((row) => {
    if (!row) {
      detail.close();
      return;
    }
    detail.select(row.inventoryId);
  }, [detail]);

  const createMember = useCreateMemberInventory({ fetchList: refreshCurrentList });
  const createAdmin = useCreateAdminInventory({ fetchList: refreshCurrentList });
  const schedule = useInventorySchedules({
    onCloneMember: createMember.openDialog,
    onCloneAdmin: createAdmin.openDialog,
  });

  return {
    inventoryList,
    pageInfo,
    isListLoading,
    listError,
    onPageChange,
    onSelectInventory,
    detail,
    report,
    schedule,
    createMember,
    createAdmin,
  };
};

import { useCallback, useState } from 'react';
import { useDepreciationStatus } from '@/features/depreciation/status';
import { useDepreciationSchedule } from '@/features/depreciation/schedule';
import { useDepreciationConfirmation } from '@/features/depreciation/confirmation';
import { winiDate } from '@/shared/lib';

const currentYear = winiDate.now().year();

export const useDepreciationManagementPage = () => {
  const [searchDraft, setSearchDraft] = useState({
    fiscalYear: currentYear,
    quarter: 'Q1',
  });
  const [searchApplied, setSearchApplied] = useState({
    fiscalYear: currentYear,
    quarter: 'Q1',
  });

  const onSearchChange = useCallback((e) => {
    const { name, value } = e.target;
    setSearchDraft((prev) => ({ ...prev, [name]: value }));
  }, []);

  const { rows, summary, isLoading, refetch } = useDepreciationStatus(
    searchApplied.fiscalYear,
    searchApplied.quarter,
  );

  const onSelect = useCallback(() => {
    const isSameCondition =
      searchDraft.fiscalYear === searchApplied.fiscalYear &&
      searchDraft.quarter === searchApplied.quarter;

    if (isSameCondition) {
      refetch();
      return;
    }

    setSearchApplied({ ...searchDraft });
  }, [searchApplied, searchDraft, refetch]);

  const scheduleCtl = useDepreciationSchedule();
  const { openSchedule } = scheduleCtl;

  const onRowSelect = useCallback(
    (row) => {
      openSchedule(row, searchApplied.fiscalYear);
    },
    [openSchedule, searchApplied.fiscalYear],
  );

  const confirmationCtl = useDepreciationConfirmation({
    fiscalYear: searchApplied.fiscalYear,
    quarter: searchApplied.quarter,
    onChanged: refetch,
  });

  return {
    searchData: searchDraft,
    onSearchChange,
    onSelect,

    rows,
    summary,
    isLoading,
    onRowSelect,

    schedule: scheduleCtl,
    confirmation: confirmationCtl,
  };
};

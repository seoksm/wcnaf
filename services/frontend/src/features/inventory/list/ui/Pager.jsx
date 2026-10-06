import { WiniBox, WiniButton, WiniTypography } from '@/shared/ui/wini';

/**
 * 전수조사 목록 - 서버 사이드 페이지네이션 컨트롤 (page는 0부터).
 */
export const Pager = ({ pageInfo, onPageChange, isLoading }) => {
  const { currentPage, totalPages, totalElements } = pageInfo || {};
  const safeTotalPages = totalPages || 0;

  if (safeTotalPages <= 1 && !totalElements) return null;

  return (
    <WiniBox ui="noAutoGap" className="flex items-center justify-between mt-2">
      <WiniTypography variant="span" className="text-text-sub text-sm">
        총 {totalElements ?? 0}건 - {safeTotalPages === 0 ? 0 : (currentPage ?? 0) + 1} / {safeTotalPages} 페이지
      </WiniTypography>
      <WiniBox ui="noAutoGap" className="flex gap-2">
        <WiniButton
          ui="lineGray"
          disabled={isLoading || (currentPage ?? 0) <= 0}
          onClick={() => onPageChange((currentPage ?? 0) - 1)}
        >
          이전
        </WiniButton>
        <WiniButton
          ui="lineGray"
          disabled={isLoading || (currentPage ?? 0) + 1 >= safeTotalPages}
          onClick={() => onPageChange((currentPage ?? 0) + 1)}
        >
          다음
        </WiniButton>
      </WiniBox>
    </WiniBox>
  );
};

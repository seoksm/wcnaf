import { useCallback, useMemo, useRef, useState, forwardRef } from 'react';
import { AgGridReact } from 'ag-grid-react';
import { themeQuartz } from 'ag-grid-community';
import PaginationItem from '@mui/material/PaginationItem';
import { cn } from '@/shared/lib/cn';
import { getUiTokens } from '@/shared/config/theme/uiTokens';
import { color } from '@/shared/config/theme';
import WiniPagination from '../Pagination/WiniPagination';

const gridStyleObject = {
  '.ag-root-wrapper': {
    borderRadius: '0',
    border: '0',
    borderTop: `1px solid ${color.border.main}`,
    borderBottom: `1px solid ${color.border.main}`,
  },
  '.ag-header': {
    backgroundColor: '#F4F9FD',
  },
  '.ag-header-row .ag-header-cell': {
    marginLeft: '2px',
  },
  '.ag-header-row .ag-header-cell:last-child .ag-header-cell-resize::after': {
    display: 'none',
  },
  '.ag-header-cell-resize::after': {
    position: 'absolute',
    top: '0',
    bottom: '0',
    height: '100%',
    width: '1px',
    zIndex: '1',
    content: '""',
    backgroundColor: '#dddddd',
    right: '0',
  },
  '.ag-ltr .ag-cell:not(.ag-cell-inline-editing), .ag-ltr .ag-full-width-row .ag-cell-wrapper.ag-row-group':
    {
      borderRight: '1px solid #dddddd',
    },
  '.ag-body .ag-row .ag-cell:not([aria-colindex="1"]):last-child': {
    borderRight: '0',
  },
  '.ag-ltr .ag-cell-focus:not(.ag-cell-range-selected):focus-within': {
    borderColor: color.border.main,
  },
  ' .ag-row-selected:before,  .ag-row-hover:not(.ag-full-width-row):before': {
    backgroundColor: '#E5EBF1',
  },
};

function styleObjectToCss(selectorPrefix, styleObj) {
  let css = '';
  for (const selector in styleObj) {
    css += `${selectorPrefix} ${selector}{`;
    const rules = styleObj[selector];
    for (const prop in rules) {
      const value = rules[prop];
      const kebab = prop.replace(/[A-Z]/g, (m) => '-' + m.toLowerCase());
      css += `${kebab}:${value};`;
    }
    css += '}';
  }
  return css;
}

const WiniAgGridReact = forwardRef(
  (
    {
      className,
      ui = 'default',
      style,
      rowSelection,
      headerHeight,
      rowHeight,
      suppressDragLeaveHidesColumns,
      paginationUi,
      paginationPageSizeSelector,
      suppressPaginationPanel,
      onGridReady,
      onPaginationChanged,
      onFirstDataRendered,
      onModelUpdated,
      ...props
    },
    ref,
  ) => {
    const uiTokens = getUiTokens(ui, 'default');
    const wrapperClass = cn(
      'winicomponent winiaggridreact',
      uiTokens.map((token) => `winiaggridreact--${token}`),
      'ag-theme-quartz',
      className,
    );

    const gridCss = useMemo(
      () => styleObjectToCss('.winiaggridreact', gridStyleObject),
      [],
    );

    let rowSelectionConfig = {
      mode: 'singleRow',
      enableClickSelection: true,
      checkboxes: false,
      headerCheckbox: false,
    };

    if (rowSelection) {
      if (typeof rowSelection === 'string') {
        rowSelectionConfig = {
          mode: rowSelection === 'multiple' ? 'multiRow' : 'singleRow',
          enableClickSelection: true,
          checkboxes: false,
          headerCheckbox: false,
        };
      } else {
        rowSelectionConfig = {
          ...rowSelection,
          enableClickSelection: rowSelection.enableClickSelection ?? true,
          checkboxes: rowSelection.checkboxes ?? false,
          headerCheckbox: rowSelection.headerCheckbox ?? false,
        };
      }
    }

    const resolvedPaginationUi =
      props.pagination === true ? (paginationUi ?? 'number') : 'default';

    const isNumberPagination = resolvedPaginationUi === 'number';
    const isFractionPagination = resolvedPaginationUi === 'fraction';
    const isCustomPagination = isNumberPagination || isFractionPagination;

    const [paginationState, setPaginationState] = useState({
      currentPage: 0,
      totalPages: 0,
    });
    const gridApiRef = useRef(null);

    const updatePaginationState = useCallback(
      (api) => {
        if (!api || props.pagination !== true) {
          setPaginationState((prev) =>
            prev.currentPage === 0 && prev.totalPages === 0
              ? prev
              : { currentPage: 0, totalPages: 0 },
          );
          return;
        }

        const nextCurrentPage = api.paginationGetCurrentPage?.() ?? 0;
        const nextTotalPages = api.paginationGetTotalPages?.() ?? 0;

        setPaginationState((prev) =>
          prev.currentPage === nextCurrentPage &&
          prev.totalPages === nextTotalPages
            ? prev
            : { currentPage: nextCurrentPage, totalPages: nextTotalPages },
        );
      },
      [props.pagination],
    );

    const handleGridReady = useCallback(
      (event) => {
        gridApiRef.current = event?.api ?? null;
        updatePaginationState(event?.api);
        if (typeof onGridReady === 'function') {
          onGridReady(event);
        }
      },
      [onGridReady, updatePaginationState],
    );

    const handlePaginationChanged = useCallback(
      (event) => {
        updatePaginationState(event?.api ?? gridApiRef.current);
        if (typeof onPaginationChanged === 'function') {
          onPaginationChanged(event);
        }
      },
      [onPaginationChanged, updatePaginationState],
    );

    const handleFirstDataRendered = useCallback(
      (event) => {
        updatePaginationState(event?.api ?? gridApiRef.current);
        if (typeof onFirstDataRendered === 'function') {
          onFirstDataRendered(event);
        }
      },
      [onFirstDataRendered, updatePaginationState],
    );

    const handleModelUpdated = useCallback(
      (event) => {
        updatePaginationState(event?.api ?? gridApiRef.current);
        if (typeof onModelUpdated === 'function') {
          onModelUpdated(event);
        }
      },
      [onModelUpdated, updatePaginationState],
    );

    const goToPage = (pageIndex) => {
      if (!gridApiRef.current) {
        return;
      }
      gridApiRef.current.paginationGoToPage(pageIndex);
    };

    const resolvedSuppressPaginationPanel = isCustomPagination
      ? true
      : suppressPaginationPanel;

    const resolvedPaginationPageSizeSelector = isCustomPagination
      ? false
      : paginationPageSizeSelector;

    return (
      <div
        className={wrapperClass}
        style={{
          height: '100%',
          width: '100%',
          minHeight: '300px',
          display: 'flex',
          flexDirection: 'column',
          ...style,
        }}
      >
        <style>{gridCss}</style>

        <div style={{ flex: 1, minHeight: 0 }}>
          <AgGridReact
            ref={ref}
            theme={themeQuartz}
            headerHeight={headerHeight ?? 40}
            rowHeight={rowHeight ?? 36}
            suppressDragLeaveHidesColumns={
              suppressDragLeaveHidesColumns ?? true
            }
            overlayNoRowsTemplate={
              '<span class="ag-overlay-no-rows-center">조회할 데이터가 없습니다.</span>'
            }
            loadThemeGoogleFonts={false}
            onGridReady={handleGridReady}
            onPaginationChanged={handlePaginationChanged}
            onFirstDataRendered={handleFirstDataRendered}
            onModelUpdated={handleModelUpdated}
            suppressPaginationPanel={resolvedSuppressPaginationPanel}
            paginationPageSizeSelector={resolvedPaginationPageSizeSelector}
            {...props}
            rowSelection={rowSelectionConfig}
          />
        </div>

        {isCustomPagination && paginationState.totalPages > 0 && (
          <div
            className="winiaggridreact-pagination"
            style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '4px',
              paddingTop: '10px',
              paddingBottom: '2px',
            }}
          >
            {isNumberPagination ? (
              <WiniPagination
                count={paginationState.totalPages}
                page={paginationState.currentPage + 1}
                size="small"
                onChange={(_event, page) => goToPage(page - 1)}
              />
            ) : (
              <WiniPagination
                count={paginationState.totalPages}
                page={paginationState.currentPage + 1}
                size="small"
                siblingCount={0}
                boundaryCount={0}
                renderItem={(item) => {
                  if (
                    item.type === 'start-ellipsis' ||
                    item.type === 'end-ellipsis'
                  ) {
                    return null;
                  }

                  if (item.type === 'page' && isFractionPagination) {
                    if (item.page !== paginationState.currentPage + 1) {
                      return null;
                    }
                    return (
                      <PaginationItem
                        {...item}
                        page={`${paginationState.currentPage + 1} / ${paginationState.totalPages}`}
                        disabled
                        sx={{
                          minWidth: 72,
                          cursor: 'default',
                          color: '#333 !important',
                          WebkitTextFillColor: '#333',
                          backgroundColor: 'transparent !important',
                          '&.Mui-selected': {
                            backgroundColor: 'transparent !important',
                            color: '#333 !important',
                            WebkitTextFillColor: '#333',
                          },
                          '&.Mui-selected:hover': {
                            backgroundColor: 'transparent !important',
                            color: '#333 !important',
                            WebkitTextFillColor: '#333',
                          },
                          '&.Mui-disabled': {
                            opacity: 1,
                            color: '#333 !important',
                            WebkitTextFillColor: '#333',
                          },
                          '&.Mui-selected.Mui-disabled': {
                            color: '#333 !important',
                            WebkitTextFillColor: '#333',
                          },
                        }}
                      />
                    );
                  }

                  return <PaginationItem {...item} />;
                }}
                onChange={(_event, page) => goToPage(page - 1)}
              />
            )}
          </div>
        )}
      </div>
    );
  },
);

export default WiniAgGridReact;

import { WiniBox, WiniTreeView, WiniTreeCheckItem } from '@/shared/ui/wini';

/**
 * 메뉴 트리 뷰
 */
export const TreeView = ({
  menu,
  onSelect,
  onChange,
  onToggleCheck,
  height = 450,
  className,
}) => {
  return (
    <WiniBox className="border border-[#c6c3c3] mt-4 mb-2 rounded p-2">
      <WiniTreeView
        className={className}
        winiData={menu}
        height={height}
        disableDrag={false}
        onSelect={onSelect}
        onChange={onChange}
      >
        {(props) => (
          <WiniTreeCheckItem
            {...props}
            branch={{ column: 'menuType', value: 'MENU' }}
            field={'chk'}
            toggleCheck={onToggleCheck}
          />
        )}
      </WiniTreeView>
    </WiniBox>
  );
};

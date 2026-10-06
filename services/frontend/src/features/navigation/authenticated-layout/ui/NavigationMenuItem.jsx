import { Fragment } from 'react';
import {
  WiniListItem,
  WiniListItemText,
  WiniTypography,
  WiniList,
  WiniIcon,
} from '@/shared/ui/wini';
import Collapse from '@mui/material/Collapse';
import { ExpandLess, ExpandMore } from '@/shared/lib';
import ico_gnb_default from '@/shared/assets/images/new/ico-menu03.png';

/**
 * 재귀 메뉴 아이템
 */
export function NavigationMenuItem({ item, idx, selectedMenu, menulist, onMenuClick, onMenuSelect, isDrawerOpen }) {
  const depth = menulist.info.filter((menu) => menu._id === item.id)[0]?.depth + 1 || 1;

  const renderMenuTypeItem = () => {
    return (
      <WiniListItem
        className={`cursor-pointer pl-[${depth * 16}px]  pt-[5px] pb-[5px]`}
        style={{ paddingLeft: depth * 16 }}
        key={item.id + '_menu'}
        onClick={() => onMenuClick(item)}
      >
        <WiniIcon icon="util" className="w-[24px] h-[24px] mr-[3px] text-[#00407f] fill-[#00407f]" />
          {/* src={ico_gnb_default}
          className="w-[24px] h-[24px] mr-[3px]" */}
          {/* alt="menu icon" */}
        {/* /> */}
        <WiniListItemText
          primary={
            <WiniTypography
              className={`text-[16px] font-bold text-[#00407f] ${isDrawerOpen ? 'inline' : 'hidden'}`}
            >
              {item.name}
            </WiniTypography>
          }
        />
        {item.isOpen ? (
          <ExpandLess className={isDrawerOpen ? 'visible' : 'invisible'} />
        ) : (
          <ExpandMore className={isDrawerOpen ? 'visible' : 'invisible'} />
        )}
      </WiniListItem>
    );
  };

  const renderProgramTypeItem = () => {
    const isActive = selectedMenu !== '' && selectedMenu === item.id;

    return (
      <WiniListItem
        key={item.id + '_pg'}
        className={`cursor-pointer py-[5px] mt-1 ${isActive ? 'bg-[#00407f]' : 'bg-transparent'}`}
        style={{ paddingLeft: depth * 20 }}
        onClick={() => onMenuSelect(item.programMapping, item.id)}
      >
        <WiniListItemText
          primary={
            <WiniTypography
              className={`text-[16px] ${isActive ? 'text-white' : 'text-[#333]'}`}
            >
              {item.name}
            </WiniTypography>
          }
        />
      </WiniListItem>
    );
  };

  return (
    <Fragment key={item.id}>
      {item.menuType === 'MENU' ? renderMenuTypeItem() : renderProgramTypeItem()}
      {item.children && item.children.length > 0 ? (
        <Collapse in={item.isOpen} timeout="auto" unmountOnExit>
          <WiniList component="div" disablePadding>
            {item.children.map((childItem, cidx) => (
              <NavigationMenuItem
                key={childItem.id + '_' + cidx}
                item={childItem}
                idx={cidx}
                selectedMenu={selectedMenu}
                menulist={menulist}
                onMenuClick={onMenuClick}
                onMenuSelect={onMenuSelect}
                isDrawerOpen={isDrawerOpen}
              />
            ))}
          </WiniList>
        </Collapse>
      ) : null}
    </Fragment>
  );
}

import { WiniStack, WiniTypography, WiniBox, WiniIcon, WiniIconButton } from '@/shared/ui/wini';
import { HomeIcon, LabelImportantIcon, MenuIcon } from '@/shared/lib';
import { DrawerHeader } from '@/shared/ui';

/**
 * PageHeader - 인증 레이아웃용 상단 헤더
 */
export function PageHeader({ title, breadcrumb, isDrawerOpen, isMobile = false, onOpenDrawer }) {
  const breadcrumbParts = (() => {
    if (breadcrumb === undefined || breadcrumb === null) {
      return [];
    }
    if (Array.isArray(breadcrumb)) {
      return breadcrumb.map((v) => String(v).trim()).filter(Boolean);
    }
    const raw = String(breadcrumb).trim();
    if (!raw) {
      return [];
    }
    const separator = raw.includes('>') ? '>' : raw.includes('/') ? '/' : null;
    if (!separator) {
      return [raw];
    }
    return raw
      .split(separator)
      .map((v) => v.trim())
      .filter(Boolean);
  })();

  return (
    <DrawerHeader
      className={`${isMobile ? 'pl-3' : isDrawerOpen ? 'pl-[315px]' : 'pl-[88px]'} bg-white pr-6 z-10`}
    >
      <WiniBox
        className='flex flex-wrap justify-between items-center w-full gap-x-4 gap-y-1'
      >
        <WiniBox className='flex items-center gap-2'>
          {isMobile && (
            <WiniIconButton onClick={onOpenDrawer} size="small" aria-label="메뉴 열기">
              <MenuIcon />
            </WiniIconButton>
          )}
          <WiniTypography
            variant="h1"
          >
            {title}
          </WiniTypography>
        </WiniBox>

        <WiniStack direction={'row'} alignItems={'center'} flexWrap={'wrap'} gap={1}>
          <WiniBox className='flex items-center'>
            <WiniIcon icon="home" className='text-text-default mr-1' sx={{ width: 20, height: 20 }} />
            <WiniTypography variant='span' className='text-text-default'>
              HOME
            </WiniTypography>
          </WiniBox>

          {breadcrumbParts.map((part, idx) => (
            <WiniStack key={`${part}-${idx}`} direction={'row'} alignItems={'center'}>
              <WiniIcon
                icon="right"
                className='text-text-default mr-2'
                sx={{ width: 20, height: 20 }}
              />
              <WiniTypography variant='span' className='text-text-default'>
                {part}
              </WiniTypography>
            </WiniStack>
          ))}
        </WiniStack>
      </WiniBox>
    </DrawerHeader>
  );
}

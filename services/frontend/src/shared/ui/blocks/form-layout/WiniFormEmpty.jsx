import { Fragment, useEffect } from 'react';

import { WiniBox, WiniCard, WiniBreadcrumbs, WiniDivider, WiniTypography } from '@/shared/ui/wini';
import { NavigateNextIcon } from '@/shared/lib';
/**
 * WiniFormEmpty  아무것도 없는 화면
 * @returns dom
 */
export default function WiniFormEmpty({ children, ...props }) {
  useEffect(() => {}, []);
  const rendBreadcrumbs = (path, index) => {
    return (
      <WiniTypography color="text.primary" key={path + index}>
        {' '}
        {path}
      </WiniTypography>
    );
  };
  return <Fragment>{children}</Fragment>;
}

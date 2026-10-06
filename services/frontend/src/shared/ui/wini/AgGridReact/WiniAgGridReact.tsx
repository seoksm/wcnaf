import { AgGridReactProps } from 'ag-grid-react';

type WiniUiInput = string | string[];

export interface WiniAgGridReactProps extends AgGridReactProps {
  ui?: WiniUiInput;
  paginationUi?: 'default' | 'number' | 'fraction';
}

declare function WiniAgGridReact(props: WiniAgGridReactProps): JSX.Element;

export default WiniAgGridReact;

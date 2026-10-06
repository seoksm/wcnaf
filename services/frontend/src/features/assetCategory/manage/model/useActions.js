import { useCallback, useRef } from 'react';
import {
  createAssetCategory,
  updateAssetCategory,
  deleteAssetCategory,
} from '../api/api';
import { winiCom } from '@/shared/lib';

export const useAssetCategoryActions = () => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);

  connectorRef.current = connector;

  const create = useCallback(async (body) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return null;
    return createAssetCategory(currentConnector, body);
  }, []);

  const update = useCallback(async (categoryId, body) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return null;
    return updateAssetCategory(currentConnector, categoryId, body);
  }, []);

  const remove = useCallback(async (categoryId) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return null;
    return deleteAssetCategory(currentConnector, categoryId);
  }, []);

  return {
    create,
    update,
    remove,
  };
};

import { useCallback, useRef } from 'react';
import {
  createAssetLocation,
  updateAssetLocation,
  deleteAssetLocation,
} from '../api/api';
import { winiCom } from '@/shared/lib';

export const useAssetLocationActions = () => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);

  connectorRef.current = connector;

  const create = useCallback(async (body) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return null;
    return createAssetLocation(currentConnector, body);
  }, []);

  const update = useCallback(async (locationId, body) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return null;
    return updateAssetLocation(currentConnector, locationId, body);
  }, []);

  const remove = useCallback(async (locationId) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return null;
    return deleteAssetLocation(currentConnector, locationId);
  }, []);

  return {
    create,
    update,
    remove,
  };
};

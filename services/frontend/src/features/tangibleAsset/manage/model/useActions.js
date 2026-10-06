import { useCallback, useRef } from 'react';
import { createTangibleAsset, updateTangibleAsset } from '../api/api';
import { winiCom } from '@/shared/lib';

export const useTangibleAssetActions = () => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);

  connectorRef.current = connector;

  const create = useCallback(async (body) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return null;
    return createTangibleAsset(currentConnector, body);
  }, []);

  const update = useCallback(async (tangibleAssetId, body) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return null;
    return updateTangibleAsset(currentConnector, tangibleAssetId, body);
  }, []);

  return {
    create,
    update,
  };
};

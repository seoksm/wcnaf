import { useCallback, useRef } from 'react';
import { createVendor, updateVendor, deleteVendor } from '@/entities/vendor';
import { winiCom } from '@/shared/lib';

export const useVendorActions = () => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;

  const create = useCallback(async (body) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return null;
    return createVendor(currentConnector, body);
  }, []);

  const update = useCallback(async (vendorId, body) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return null;
    return updateVendor(currentConnector, vendorId, body);
  }, []);

  const remove = useCallback(async (vendorId) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return null;
    return deleteVendor(currentConnector, vendorId);
  }, []);

  return { create, update, remove };
};

# 📝 Event Specification

<div style="min-width: 100%; overflow-x: auto; margin: 20px 0;">
  <table style="width: 100%; min-width: 1000px; border-collapse: collapse; text-align: left; font-size: 14px; line-height: 1.6;">
    <thead>
      <tr style="background-color: #f6f8fa; border-bottom: 2px solid #d0d7de;">
        <th style="padding: 12px 16px; font-weight: 600; border: 1px solid #d0d7de; width: 8%; text-align: center;">구분</th>
        <th style="padding: 12px 16px; font-weight: 600; border: 1px solid #d0d7de; width: 22%;">이벤트명 / 설명</th>
        <th style="padding: 12px 16px; font-weight: 600; border: 1px solid #d0d7de; width: 25%;">토픽 (Topic)</th>
        <th style="padding: 12px 16px; font-weight: 600; border: 1px solid #d0d7de; width: 35%;">DTO 상세 ({ key : type })</th>
        <th style="padding: 12px 16px; font-weight: 600; border: 1px solid #d0d7de; width: 10%; text-align: center;">브로커</th>
      </tr>
    </thead>
    <tbody>
      <tr style="border-bottom: 1px solid #d0d7de;">
        <td style="padding: 14px 16px; vertical-align: top; border: 1px solid #d0d7de; text-align: center;"><span style="background-color:#e6ffed; color:#22863a; padding:2px 6px; border-radius:3px; font-size:12px; font-weight:bold;">SUB</span></td>
        <td style="padding: 14px 16px; vertical-align: top; border: 1px solid #d0d7de;"><strong style="color:#0969da;">CommonAuthorizationDataEvent</strong><br><span style="color:#57606a; font-size:13px;">공통권한데이터변경</span></td>
        <td style="padding: 14px 16px; vertical-align: top; font-family: monospace; background-color: #f8f9fa; border: 1px solid #d0d7de; color: #cf222e; font-weight: bold; word-break: break-all;">wini.fct.common.v1.common-authorization-data-change</td>
        <td style="padding: 14px 16px; vertical-align: top; border: 1px solid #d0d7de; font-size: 13px;">{<br>&emsp;<strong>op</strong> : <code>enum</code> <span style='color:#0969da; font-weight:bold;'>[ s : sync ]</span><br>&emsp;<strong>ts_ms</strong> : <code>Integer</code><br>&emsp;<strong>message</strong> : <code>String</code><br>}<br><br>
          <details style="cursor:pointer;">
            <summary style="color:#0969da; font-weight:bold; margin-bottom:10px;">💡 JSON Payload Example</summary>
            <pre><code class="language-json">
{
    "op": "s",
    "ts_ms": 0,
    "message": "string"
}
</code></pre>
          </details>
        </td>
        <td style="padding: 14px 16px; vertical-align: top; text-align: center; border: 1px solid #d0d7de;"><span style="background-color: #dafbe1; color: #1a7f37; padding: 4px 8px; border-radius: 6px; font-size: 12px; font-weight: 600;">kafka</span></td>
      </tr>
      <tr style="border-bottom: 1px solid #d0d7de;">
        <td style="padding: 14px 16px; vertical-align: top; border: 1px solid #d0d7de; text-align: center;"><span style="background-color:#e6ffed; color:#22863a; padding:2px 6px; border-radius:3px; font-size:12px; font-weight:bold;">SUB</span></td>
        <td style="padding: 14px 16px; vertical-align: top; border: 1px solid #d0d7de;"><strong style="color:#0969da;">RouteChangeEvent</strong><br><span style="color:#57606a; font-size:13px;">라우트변경</span></td>
        <td style="padding: 14px 16px; vertical-align: top; font-family: monospace; background-color: #f8f9fa; border: 1px solid #d0d7de; color: #cf222e; font-weight: bold; word-break: break-all;">wini.fct.api-gateway.v1.route-change</td>
        <td style="padding: 14px 16px; vertical-align: top; border: 1px solid #d0d7de; font-size: 13px;">{<br>&emsp;<strong>ts_ms</strong> : <code>Integer</code><br>}<br><br>
          <details style="cursor:pointer;">
            <summary style="color:#0969da; font-weight:bold; margin-bottom:10px;">💡 JSON Payload Example</summary>
            <pre><code class="language-json">
{
    "ts_ms": 0
}
</code></pre>
          </details>
        </td>
        <td style="padding: 14px 16px; vertical-align: top; text-align: center; border: 1px solid #d0d7de;"><span style="background-color: #dafbe1; color: #1a7f37; padding: 4px 8px; border-radius: 6px; font-size: 12px; font-weight: 600;">kafka</span></td>
      </tr>
      <tr style="border-bottom: 1px solid #d0d7de;">
        <td style="padding: 14px 16px; vertical-align: top; border: 1px solid #d0d7de; text-align: center;"><span style="background-color:#e6ffed; color:#22863a; padding:2px 6px; border-radius:3px; font-size:12px; font-weight:bold;">SUB</span></td>
        <td style="padding: 14px 16px; vertical-align: top; border: 1px solid #d0d7de;"><strong style="color:#0969da;">UserSessionBlockEvent</strong><br><span style="color:#57606a; font-size:13px;">세션블락이벤트</span></td>
        <td style="padding: 14px 16px; vertical-align: top; font-family: monospace; background-color: #f8f9fa; border: 1px solid #d0d7de; color: #cf222e; font-weight: bold; word-break: break-all;">wini.fct.common.v1.common-user-token-block</td>
        <td style="padding: 14px 16px; vertical-align: top; border: 1px solid #d0d7de; font-size: 13px;">{<br>&emsp;<strong>op</strong> : <code>enum</code> <span style='color:#0969da; font-weight:bold;'>[ s : sync ]</span><br>&emsp;<strong>ts_ms</strong> : <code>Integer</code><br>&emsp;<strong>userSessionId</strong> : <code>UUID</code><br>&emsp;<strong>accessTokenExpiresAt</strong> : <code>OffsetDateTime</code><br>&emsp;<strong>userId</strong> : <code>UUID</code><br>}<br><br>
          <details style="cursor:pointer;">
            <summary style="color:#0969da; font-weight:bold; margin-bottom:10px;">💡 JSON Payload Example</summary>
            <pre><code class="language-json">
{
    "op": "s",
    "ts_ms": 0,
    "userSessionId": "00000000-0000-0000-0000-000000000000",
    "accessTokenExpiresAt": null,
    "userId": "00000000-0000-0000-0000-000000000000"
}
</code></pre>
          </details>
        </td>
        <td style="padding: 14px 16px; vertical-align: top; text-align: center; border: 1px solid #d0d7de;"><span style="background-color: #dafbe1; color: #1a7f37; padding: 4px 8px; border-radius: 6px; font-size: 12px; font-weight: 600;">kafka</span></td>
      </tr>
      <tr style="border-bottom: 1px solid #d0d7de;">
        <td style="padding: 14px 16px; vertical-align: top; border: 1px solid #d0d7de; text-align: center;"><span style="background-color:#dbedff; color:#0366d6; padding:2px 6px; border-radius:3px; font-size:12px; font-weight:bold;">PUB</span></td>
        <td style="padding: 14px 16px; vertical-align: top; border: 1px solid #d0d7de;"><strong style="color:#0969da;">RouteChangeEvent</strong><br><span style="color:#57606a; font-size:13px;">라우팅변경전달</span></td>
        <td style="padding: 14px 16px; vertical-align: top; font-family: monospace; background-color: #f8f9fa; border: 1px solid #d0d7de; color: #cf222e; font-weight: bold; word-break: break-all;">wini.fct.api-gateway.v1.route-change</td>
        <td style="padding: 14px 16px; vertical-align: top; border: 1px solid #d0d7de; font-size: 13px;">{<br>&emsp;<strong>ts_ms</strong> : <code>Integer</code><br>}<br><br>
          <details style="cursor:pointer;">
            <summary style="color:#0969da; font-weight:bold; margin-bottom:10px;">💡 JSON Payload Example</summary>
            <pre><code class="language-json">
{
    "ts_ms": 0
}
</code></pre>
          </details>
        </td>
        <td style="padding: 14px 16px; vertical-align: top; text-align: center; border: 1px solid #d0d7de;"><span style="background-color: #dafbe1; color: #1a7f37; padding: 4px 8px; border-radius: 6px; font-size: 12px; font-weight: 600;">kafka</span></td>
      </tr>
    </tbody>
  </table>
</div>

---

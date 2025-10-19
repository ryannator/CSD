export interface JwtPayload {
  sub?: string
  roles?: string | string[]
  [key: string]: unknown
}

function base64UrlDecode(input: string): string {
  const base64 = input.replace(/-/g, '+').replace(/_/g, '/')
  const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=')
  try {
    return atob(padded)
  } catch (error) {
    throw new Error('Invalid base64 input')
  }
}

export function decodeJwt(token: string): JwtPayload | null {
  if (!token) return null

  const parts = token.split('.')
  if (parts.length < 2) return null

  try {
    const payload = base64UrlDecode(parts[1])
    return JSON.parse(
      decodeURIComponent(
        Array.from(payload)
          .map((char) => `%${char.charCodeAt(0).toString(16).padStart(2, '0')}`)
          .join('')
      )
    )
  } catch (error) {
    console.error('Failed to decode JWT payload:', error)
    return null
  }
}

export function getRolesFromToken(token: string): string[] {
  const payload = decodeJwt(token)
  if (!payload || !payload.roles) {
    return []
  }

  const { roles } = payload

  const rawRoles = Array.isArray(roles) ? roles : String(roles).split(',')
  return rawRoles.map((role) => role.trim()).filter(Boolean)
}

export function getEmailFromToken(token: string): string | undefined {
  return decodeJwt(token)?.sub as string | undefined
}

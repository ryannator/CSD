import client from './client';

export async function signin(email: string, password: string) {
  const { data } = await client.post('/auth/signin', { email, password });

  return data as { token: string };
}

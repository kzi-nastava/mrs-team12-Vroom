export interface RegisterRequestDTO {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  address: string;
  gender: 'MALE' | 'FEMALE' | 'OTHER'; 
  password: string;
  profilePhoto?: string;
  type: 'user' | 'driver' | 'admin';
}